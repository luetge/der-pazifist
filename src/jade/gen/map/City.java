package jade.gen.map;

import jade.core.World;
import jade.util.datatype.Door;
import jade.util.Dice;
import jade.util.Guard;
import jade.util.datatype.ColoredChar;
import java.util.LinkedList;
import java.awt.Rectangle;
import java.awt.Color;
import jade.gen.map.AsciiMap;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * Uses a binary space partitioning algorithm to generate rooms, and the connect them using the
 * binary space partition tree. Cycles are then added to make the maps more interesting. This
 * algorithm yields traditional roguelike maps with rectangular rooms connected by corridors.
 */
public class City extends MapGenerator
{
    private ColoredChar wallTile;
    private ColoredChar floorTile;
    private int minWidth;
    private int minHeight;
    private AsciiMap church[];
    private AsciiMap bunker;
    private LinkedList<Rectangle> reserved;
    private int housenum;

    /**
     * Instantiates a BSP with default parameters. Room minSize is 4. Wall and floor tiles are '#'
     * and '.' respectively.
     */
    public City()
    {
        this(ColoredChar.create(' '), ColoredChar.create('#'), 16, 16*3/4);
    }

    /**
     * Instantiates a custom BSP with provided parameters.
     * @param floorTile the tile used for impassible walls
     * @param wallTile the tile used for passable floors
     * @param minSize the minimum dimension of a room
     */
    public City(ColoredChar floorTile, ColoredChar wallTile, int minWidth, int minHeight)
    {
    	this.housenum = 0;
        this.floorTile = floorTile;
        this.wallTile = wallTile;
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.church = new AsciiMap[3];
        this.reserved = new LinkedList<Rectangle>();
        
        for (int i = 0; i < 3; i++)
        	this.church[i] = new AsciiMap ("res/maps/church" + i + ".txt");
        
        this.bunker = new AsciiMap("res/maps/bunker.txt");
    }
    
    private Rectangle getRandomRect (World world, Dice dice, int width, int height)
    {
    	Rectangle rect;
    	int posx, posy;
    	do
    	{
    		posx = dice.nextInt (0, world.width() - width - 1);
    		posy = dice.nextInt (0, world.height() - height - 1);
    		rect = new Rectangle (posx, posy, width, height);
    		// TODO: make sure this loop ends
    	} while (checkReserved (rect));
    	reserved.add(rect);
    	return rect;
    }
    
    private boolean checkReserved (Rectangle rect)
    {
    	for (Rectangle resrect : reserved)
    	{
    		if (rect.intersects (resrect))
    			return true;
    	}
    	return false;
    }
    
    private boolean checkReserved (int x, int y, int width, int height)
    {
    	return checkReserved (new Rectangle(x, y, width, height));
    }
    
    private void generateSpecialRoom (World world, Dice dice, AsciiMap map)
    {
    	Rectangle rect = getRandomRect (world, dice, map.width() + 2, map.height() + 2);
    	map.render (world, rect.x + 1, rect.y + 1);
    	Map<Coordinate, Door> doors = map.getDoors();
		for (Coordinate coord : doors.keySet())
    	{
			// d hat coordinaten relativ zur AsciiMap
			Door d = doors.get(coord);
			Door door = new Door(d.getID(), rect.x + 1 + coord.x(), rect.y + 1 + coord.y(),
					d.getDestWorld(), d.getDestID(), d.getDirection());
			world.addDoor(rect.x + 1 + coord.x(), rect.y + 1 + coord.y(), door);
    	}
    }
    
    @Override
    protected void generateStep(World world, Dice dice)
    {
        floorFill(world);

        generateSpecialRoom(world, dice, bunker);
        
        for (int i = 0; i < church.length; i++)
        	generateSpecialRoom(world, dice, church[i]);
        
        
       	BSPNode head = new BSPNode(world);
        	
        head.divide(dice, minWidth, minHeight);
        	
        head.makeRooms(world, dice, minWidth, minHeight);
    }
    
    private void floorFill(World world)
    {
        // Fence
    	for(int x=0; x < world.width(); x++){
        	world.setTile(wallTile, false, x, 0, true);
        	world.setTile(wallTile, false, x, world.height()-1, true);
        }
        for(int y=0; y < world.height(); y++){
        	world.setTile(wallTile, false, 0, y, true);
        	world.setTile(wallTile,  false, world.width()-1, y, true);
        }
    }

    private class BSPNode
    {
        private int x1;
        private int y1;
        private int x2;
        private int y2;
        private int rx1;
        private int ry1;
        private int rx2;
        private int ry2;
        private BSPNode left;
        private BSPNode right;

        public BSPNode(World world)
        {
            x1 = 0;
            y1 = 0;
            x2 = world.width() - 1;
            y2 = world.height() - 2;
        }

        private BSPNode(BSPNode parent, int div, boolean vert, boolean left)
        {
            // vert means we divide on x
            x1 = parent.x1 + (vert && !left ? div + 1 : 0);
            x2 = vert && left ? parent.x1 + div : parent.x2;
            // non vert means we divide on y
            y1 = parent.y1 + (!vert && !left ? div + 1 : 0);
            y2 = !vert && left ? parent.y1 + div : parent.y2;
        }
        
        public void divide(Dice dice, int minw, int minh)
        {
        	if (leaf())
        	{
        		boolean vert = dice.chance();
        		int min = (vert ? minw : minh) + 4;// +4 so we don't get aligned grid of rooms
        		if(divTooSmall(vert, min))
        		{
        			vert = !vert;
        			min = (vert ? minw : minh) + 4;
        		}
        		if(divTooSmall(vert, min))
        			return;
        		int div = dice.nextInt(min, (vert ? x2 - x1 : y2 - y1) - min);
        		left = new BSPNode(this, div, vert, true);
        		right = new BSPNode(this, div, vert, false);
        	}
            left.divide(dice, minw, minh);
            right.divide(dice, minw, minh);
        }
        
        public void makeRooms(World world, Dice dice, int minw, int minh)
        {
            if(leaf())
            {
                rx1 = dice.nextInt(x1 + 1, x2 - 1 - minw);
                rx2 = dice.nextInt(rx1 + minw, x2 - 1);
                ry1 = dice.nextInt(y1 + 1, y2 - 1 - minh);
                ry2 = dice.nextInt(ry1 + minh, y2 - 1);
                if (((rx2-rx1) & 1) == 1)
                	rx2--;
                if (((ry2-ry1) & 1) == 1)
                	ry2--;	
                
                if (checkReserved (rx1-1, ry1-1, rx2-rx1+2, ry2-ry1+2))
                	return;

                // TODO: make the edge characters global
                ColoredChar edge = new ColoredChar ('╔', Color.white);
                world.setTile(edge, false, rx1, ry1, true);
                edge = new ColoredChar ('╚', Color.white);
                world.setTile(edge, false, rx1, ry2, true);
                edge = new ColoredChar ('╗', Color.white);
                world.setTile(edge, false, rx2 , ry1, true);
                edge = new ColoredChar ('╝', Color.white);
                world.setTile(edge, false, rx2, ry2, true);

                edge = new ColoredChar ('═', Color.white);
                for(int x=rx1+1; x < rx2; x++){
                	world.setTile(edge, false, x, ry1, true);
                	world.setTile(edge, false, x, ry2, true);
                }
                edge = new ColoredChar ('║', Color.white);
                for(int y=ry1+1; y < ry2; y++){
                	world.setTile(edge, false, rx1, y, true);
                	world.setTile(edge, false, rx2, y, true);
                }
                
                // TODO: make this character global
                ColoredChar window = new ColoredChar('⊞', Color.black);
                ColoredChar windowlit = new ColoredChar('⊞', Color.yellow);
                
                float brightness = ((float)dice.nextInt (64,196))/256.0f;
                Color background = new Color (brightness,brightness,brightness);
                
                for (int x = rx1 + 1; x <= rx2 - 1; x++)
                {
                	for (int y = ry1 + 1; y <= ry2 - 1; y++)
                	{
                		if ((((x-rx1-1)&1)&((y-ry1-1)&1))==1)
                		{
                			world.setTile(dice.chance()?windowlit:window, false, x, y, true);
                		}
                		else
                		{
                			world.setTile(floorTile, false, x, y, true);
                		}
                        world.setTileBackground(background,  x, y);
                	}
                }
                
                // TODO: make this character global
                ColoredChar antenna = new ColoredChar('╧', Color.white);
                world.setTile(antenna, false, dice.nextInt(rx1+(rx2-rx1)/3,rx2-(rx2-rx1)/3), ry1, true);
                
                int doorx = dice.nextInt (rx1+(rx2-rx1)/3,rx2-(rx2-rx1)/3);
                int doory = ry2;

                // TODO: make c global
                ColoredChar c = new ColoredChar ('↑', Color.white);
                for (int x = 0; x < 3; x++)
                {
                	world.setTile(c, false, doorx+x, doory, true);
                	world.setTileBackground(new Color(0x806000).brighter(), doorx+x, doory);
                }
                for (int i = 0; i < 3; i++)
                {
                	Door door = new Door("house" + housenum + "entry" + i,
                					 doorx+i, doory, "house" + housenum, "worldentry" + i);
                	world.addDoor (doorx+i, doory, door);
                }
                housenum++;
            }
            else
            {
                left.makeRooms(world, dice, minw, minh);
                right.makeRooms(world, dice, minw, minh);
            }
        }


        private boolean divTooSmall(boolean vert, int min)
        {
            min *= 2;// need space for two rooms
            return vert ? (x2 - x1) < min : (y2 - y1) < min;
        }
        
        private boolean leaf()
        {
            return left == null && right == null;
        }
    }
}
