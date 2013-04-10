package pazi.gen.map;

import java.awt.Color;

import pazi.core.World;
import pazi.util.Dice;
import pazi.util.datatype.ColoredChar;
import pazi.util.datatype.Coordinate;
import pazi.util.datatype.Direction;
import pazi.util.datatype.Door;


public class House extends MapGenerator {
	
	public House()
	{
		
	}
	
	private void floorFill(World world)
    {

        // TODO: make the edge characters global
        ColoredChar edge = new ColoredChar ('\u2554', Color.white); // ╔
        world.setTile(edge, false, 0, 0, true);
        edge = new ColoredChar ('\u255A', Color.white); // ╚
        world.setTile(edge, false, 0, world.height()-1, true);
        edge = new ColoredChar ('\u2557', Color.white); // ╗
        world.setTile(edge, false, world.width()-1, 0, true);
        edge = new ColoredChar ('\u255d', Color.white); // ╝ 
        world.setTile(edge, false, world.width()-1, world.height()-1, true);

        edge = new ColoredChar ('\u2550', Color.white); // ═
        for(int x=1; x < world.width()-1; x++){
        	world.setTile(edge, false, x, 0, true);
        	world.setTile(edge, false, x, world.height()-1, true);
        }
        edge = new ColoredChar ('\u2551', Color.white); // ║
        for(int y=1; y < world.height()-1; y++){
        	world.setTile(edge, false, 0, y, true);
        	world.setTile(edge, false, world.width()-1, y, true);
        }
        
		ColoredChar wallTile = ColoredChar.create(' ');
		for (int x = 1; x < world.width() - 1; x++)
        {
        	for (int y = 1; y < world.height() - 1; y++)
        	{
            	world.setTile(wallTile, true, x, y, true);
            	world.setTileBackground(Color.darkGray, x, y);
        	}
        }
        
        for (int i =0; i < 3; i++)
        {
        	int doorx = world.width()/2+i;
        	int doory = world.height()-1;
        	ColoredChar doorTile = ColoredChar.create('\u2193'); // ↓
        	world.setTileBackground(new Color(0x806000).brighter(), doorx, doory);
        	world.setTile(doorTile, false, doorx, doory, true);
        }
    }
	public void addExitDoors(World world, String exitname)
	{
		for (int i = 0; i < 3; i++)
		{
			world.addDoor(world.width()/2+i,world.height()-1,
					new Door("worldentry"+i,world.width()/2+i,world.height()-1,exitname,
					world.getName()+"entry"+i, Direction.SOUTH));
		}
	}
	
    @Override
    protected void generateStep(World world, Dice dice)
    {
        floorFill(world);
    }
}
