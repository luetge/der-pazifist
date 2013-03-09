package jade.gen.map;

import jade.core.World;
import jade.util.Dice;
import jade.util.datatype.ColoredChar;
import java.awt.Color;

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
        this.floorTile = floorTile;
        this.wallTile = wallTile;
        this.minWidth = minWidth;
        this.minHeight = minHeight;
    }
    
    @Override
    protected void generateStep(World world, Dice dice)
    {
        floorFill(world);
        BSPNode head = new BSPNode(world, minWidth, minHeight);
        head.divide(dice);
        head.makeRooms(world, dice);
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

        public BSPNode(World world, int minWidth, int minHeight)
        {
            x1 = 0;
            y1 = 0;
            x2 = world.width() - 1;
            y2 = world.height() - 1;
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

        public void divide(Dice dice)
        {
            boolean vert = dice.chance();
            int min = (vert ? minWidth : minHeight) + 4;// +4 so we don't get aligned grid of rooms
            if(divTooSmall(vert, min))
            {
                vert = !vert;
                min = (vert ? minWidth : minHeight) + 4;
            }
            if(divTooSmall(vert, min))
                return;
            int div = dice.nextInt(min, (vert ? x2 - x1 : y2 - y1) - min);
            left = new BSPNode(this, div, vert, true);
            right = new BSPNode(this, div, vert, false);
            left.divide(dice);
            right.divide(dice);
        }

        public void makeRooms(World world, Dice dice)
        {
            if(leaf())
            {
                rx1 = dice.nextInt(x1 + 1, x2 - 1 - minWidth);
                rx2 = dice.nextInt(rx1 + minWidth, x2 - 1);
                ry1 = dice.nextInt(y1 + 1, y2 - 1 - minHeight);
                ry2 = dice.nextInt(ry1 + minHeight, y2 - 1);

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

                // Innenraum nicht betretbar
                for(int x=rx1+1; x < rx2; x++)
                	for(int y=ry1+1; y < ry2; y++)
                		world.setTile(floorTile, false, x, y);

                float brightness = ((float)dice.nextInt (64,196))/256.0f;
                Color background = new Color (brightness,brightness,brightness);
                
                for (int x = rx1 + 1; x <= rx2 - 1; x++)
                {
                	for (int y = ry1 + 1; y <= ry2 - 1; y++)
                	{
                        world.setTile(floorTile, false, x, y, true);
                        world.setTileBackground(background,  x, y);
                	}
                }
                
                int doorside = dice.nextInt(0,3);
                if (doorside == 0 && rx1 == 1)
                	doorside = 1;
                if (doorside == 1 && rx2 == world.width() - 2)
                	doorside = 0;
                if (doorside == 2 && ry1 == 1)
                	doorside = 3;
                if (doorside == 3 && ry2 == world.height () - 2)
                	doorside = 2;
                int doorx, doory;
                switch (doorside)
                {
                case 0:
                	doorx = rx1;
                	doory = dice.nextInt (ry1+1,ry2-2);
                	break;
                case 1:
                	doorx = rx2;
                	doory = dice.nextInt (ry1+1,ry2-2);
                	break;
                case 2:
                	doorx = dice.nextInt (rx1+1,rx2-1);
                	doory = ry1;
                	break;
                default:
                	doorx = dice.nextInt (rx1+1,rx2-1);
                	doory = ry2;
                	break;
                }

                // TODO: make c global
                ColoredChar c = new ColoredChar ('+', Color.white);
                world.setTile(c, false, doorx, doory, true);
                world.setTileBackground(Color.orange.darker().darker(), doorx, doory);
                world.addDoor (0, doorx, doory);
            }
            else
            {
                left.makeRooms(world, dice);
                right.makeRooms(world, dice);
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
