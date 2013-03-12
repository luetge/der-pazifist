package jade.gen.map;

import java.awt.Color;

import jade.core.World;
import jade.util.datatype.Door;
import jade.util.Dice;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

public class Room extends MapGenerator {
	
	public Room()
	{
		
	}
	
	private void floorFill(World world)
    {

        // TODO: make the edge characters global
        ColoredChar edge = new ColoredChar ('╔', Color.white);
        world.setTile(edge, false, 0, 0, true);
        edge = new ColoredChar ('╚', Color.white);
        world.setTile(edge, false, 0, world.height()-1, true);
        edge = new ColoredChar ('╗', Color.white);
        world.setTile(edge, false, world.width()-1, 0, true);
        edge = new ColoredChar ('╝', Color.white);
        world.setTile(edge, false, world.width()-1, world.height()-1, true);

        edge = new ColoredChar ('═', Color.white);
        for(int x=1; x < world.width()-1; x++){
        	world.setTile(edge, false, x, 0, true);
        	world.setTile(edge, false, x, world.height()-1, true);
        }
        edge = new ColoredChar ('║', Color.white);
        for(int y=1; y < world.height()-1; y++){
        	world.setTile(edge, false, 0, y, true);
        	world.setTile(edge, false, world.width()-1, y, true);
        }
        
		ColoredChar wallTile = ColoredChar.create(' ');
		for (int x = 1; x < world.width() - 2; x++)
        {
        	for (int y = 1; y < world.height() - 2; y++)
        	{
            	world.setTile(wallTile, true, x, y, true);
        	}
        }
        
        for (int i =0; i < 3; i++)
        {
        	int doorx = world.width()/2+i;
        	int doory = world.height()-1;
        	ColoredChar doorTile = ColoredChar.create('↓');
        	world.setTileBackground(new Color(0x806000).brighter(), doorx, doory);
        	world.setTile(doorTile, false, doorx, doory, true);
        }
    }
	public void addDoors(World world, String exitname)
	{
		for (int i = 0; i < 3; i++)
		{
			world.addDoor(world.width()/2+i,world.height()-1,
					new Door("roomentry"+i,world.width()/2+i,world.height()-1,exitname,
					world.getName()+"entry"+i, Direction.SOUTH));
		}
	}
	
	public void addExit(World world, Door door)
	{
		world.addDoor(4, 4, door);
	}
	
    @Override
    protected void generateStep(World world, Dice dice)
    {
        floorFill(world);
    }
}
