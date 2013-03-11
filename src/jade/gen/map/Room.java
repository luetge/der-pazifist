package jade.gen.map;

import jade.core.World;
import jade.core.Door;
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
        
        ColoredChar doorTile = ColoredChar.create('x');
        world.setTile(doorTile, false, 4, 4, true);
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
