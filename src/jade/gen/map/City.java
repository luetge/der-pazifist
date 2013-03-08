package jade.gen.map;

import jade.core.World;
import jade.util.Dice;
import jade.util.datatype.ColoredChar;

/**
 * Uses a binary space partitioning algorithm to generate rooms, and the connect them using the
 * binary space partition tree. Cycles are then added to make the maps more interesting. This
 * algorithm yields traditional roguelike maps with rectangular rooms connected by corridors.
 */
public class City extends MapGenerator
{
    private ColoredChar wallTile;
    private ColoredChar floorTile;
    private int minSize;

    /**
     * Instantiates a BSP with default parameters. Room minSize is 4. Wall and floor tiles are '#'
     * and '.' respectively.
     */
    public City()
    {
        this(ColoredChar.create(' '), ColoredChar.create('#'), 16);
    }

    /**
     * Instantiates a custom BSP with provided parameters.
     * @param floorTile the tile used for impassible walls
     * @param wallTile the tile used for passable floors
     * @param minSize the minimum dimension of a room
     */
    public City(ColoredChar floorTile, ColoredChar wallTile, int minSize)
    {
        this.floorTile = floorTile;
        this.wallTile = wallTile;
        this.minSize = minSize;
    }

    @Override
    protected void generateStep(World world, Dice dice)
    {
        floorFill(world);
        BSPNode head = new BSPNode(world, minSize);
        head.divide(dice);
        head.makeRooms(world, dice);
    }

    private void floorFill(World world)
    {
        // Fence
        makeSquare(0, 0, world.width()-1, world.height()-1, world, false);
    }
    
    protected void makeSquare(int x1, int y1, int x2, int y2, World world, boolean makeInteriorImpassable){
    	for(int x=x1; x <= x2; x++){
        	world.setTile(wallTile, false, x, y1);
        	world.setTile(wallTile, false, x, y2);
        }
        for(int y=y1; y <= y2; y++){
        	world.setTile(wallTile, false, x1, y);
        	world.setTile(wallTile, false, x2, y);
        }
        
        // Innenraum nicht betretbar
        if(makeInteriorImpassable)
        	for(int x=x1+1; x < x2; x++)
        		for(int y=y1+1; y < y2; y++)
        			world.setTile(floorTile, false, x, y);
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

        public BSPNode(World world, int minSize)
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
            int min = minSize + 4;// +4 so we don't get aligned grid of rooms
            if(divTooSmall(vert, min))
                vert = !vert;
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
                rx1 = dice.nextInt(x1 + 1, x2 - 1 - minSize);
                rx2 = dice.nextInt(rx1 + minSize, x2 - 1);
                ry1 = dice.nextInt(y1 + 1, y2 - 1 - minSize);
                ry2 = dice.nextInt(ry1 + minSize, y2 - 1);
                makeSquare(rx1, ry1, rx2, ry2, world, true);
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
