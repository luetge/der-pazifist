package jade.gen.map;

import jade.core.World;
import jade.util.datatype.Door;
import jade.util.Dice;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;

public class Room extends MapGenerator {
	
	public Room()
	{
		
	}
	
	private void floorFill(World world)
    {
        // Fence
		ColoredChar wallTile = ColoredChar.create(' ');
    	for(int x=0; x < world.width(); x++){
        	world.setTile(wallTile, false, x, 0, true);
        	world.setTile(wallTile, false, x, world.height()-1, true);
        }
        for(int y=0; y < world.height(); y++){
        	world.setTile(wallTile, false, 0, y, true);
        	world.setTile(wallTile,  false, world.width()-1, y, true);
        }
        
        for (int i =0; i < 3; i++)
        {
        	ColoredChar doorTile = ColoredChar.create('x');
        	world.setTile(doorTile, false, 4+i, 4, true);
        }
    }
	public void addDoors(World world, String exitname)
	{
		for (int i = 0; i < 3; i++)
		{
			world.addDoor(4+i,4,new Door("roomentry"+i,4+i,4,exitname,
					world.getName()+"entry"+i));
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
