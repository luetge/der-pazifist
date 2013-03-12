package rogue.level;

import jade.core.World;
import jade.util.datatype.Door;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;
import jade.gen.Generator;
import jade.gen.map.*;
import rogue.creature.Player;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class Level
{
    private final static Generator gen = getLevelGenerator();
    private final static Room roomgen = getRoomGenerator();
    
    private Map<String,World> worlds;
    
    private Player player;
    private String startname;

    public Level(int width, int height, Player player, String name)
    {
    	this.player = player;
    	this.startname = name;
    	worlds = new HashMap<String, World>();
    	World startworld = new World (width, height, name);
    	gen.generate(startworld);
    	startworld.addActor(player);
    	worlds.put(name, startworld);
    	
    	
    }
    
    public World getWorld (String name)
    {
    	return worlds.get(name);
    }
    
    public World stepToWorld (Door door)
    {
    	World w = worlds.get(door.getDestWorld());
    	if (w == null)
    	{
    		w = new World (128, 128, door.getDestWorld());
    		
    		File f = new File("res/rooms/"+door.getDestWorld());
    		if (f.exists() && !f.isDirectory())
    		{
    			AsciiMap asciimap = new AsciiMap("res/rooms/"+door.getDestWorld());
    			asciimap.render(w, 0, 0);
    			Map<Coordinate, Door> doors = asciimap.getDoors();
    			for (Coordinate coord : doors.keySet())
    			{
    				Door d = doors.get(coord);
    				d = new Door(d.getID(), d.getPosition(), d.getDestWorld(),
    						d.getDestID(), Direction.SOUTH);
    				w.addDoor(d.getPosition(), d);
    			}
    		}
    		else
    		{
    			roomgen.generate(w);
    			roomgen.addDoors (w, startname);
    		}
    		worlds.put(door.getDestWorld(), w);
    	}
    	return w;
    }

    private static Generator getLevelGenerator()
    {
    	return new City();
//        return new Cellular();
    }
    
    private static Room	 getRoomGenerator()
    {
    	return new Room();
    }
}
