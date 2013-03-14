package rogue.level;

import jade.core.World;
import jade.gen.Generator;
import jade.gen.map.AsciiMap;
import jade.gen.map.City;
import jade.gen.map.House;
import jade.util.Dice;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;
import jade.util.datatype.Door;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import pazi.items.Gold;
import rogue.creature.CreatureFactory;
import rogue.creature.Player;

public class Level
{
    private final static Generator gen = getLevelGenerator();
    private final static House housegen = getHouseGenerator();
    
    private Map<String,World> worlds;
    private World world;
    
    private Player player;
    private String startname;

    public Level(int width, int height, Player player, String name)
    {
    	this.player = player;
    	this.startname = name;
    	worlds = new HashMap<String, World>();
    	world = new World (width, height, name);
    	gen.generate(world);
    	world.addActor(player);
    	worlds.put(name, world);
    }
    
    public World world ()
    {
    	return world;
    }
    
    public World world (String name)
    {
    	return worlds.get(name);
    }
    
    private void movePlayerThroughDoor (World toworld, Door door)
    {
    	world().removeActor(player);
		Door destdoor = toworld.getDoor(door.getDestID());
		toworld.addActor(player, destdoor.getDestination());
    }
    
    public void stepThroughDoor (Door door)
    {
    	World w = worlds.get(door.getDestWorld());
    	if (w == null)
    	{
    		
    		File f = new File("res/rooms/"+door.getDestWorld());
    		if (f.exists() && !f.isDirectory())
    		{
    			AsciiMap asciimap = new AsciiMap("res/rooms/"+door.getDestWorld());
        		w = new World (asciimap.width(), asciimap.height(), door.getDestWorld());
    			asciimap.render(w, 0, 0);
    			Map<Coordinate, Door> doors = asciimap.getDoors();
    			for (Coordinate coord : doors.keySet())
    			{
    				Door d = doors.get(coord);
    				d = new Door(d.getID(), d.getPosition(), d.getDestWorld(),
    						d.getDestID(), Direction.SOUTH);
    				w.addDoor(d.getPosition(), d);
    			}
    			movePlayerThroughDoor (w, door);
    			asciimap.addCreatures(w);
    		}
    		else
    		{
        		int size = Dice.global.nextInt(25, 30);
        		w = new World (size, size*3/4, door.getDestWorld());
    			housegen.generate(w);
    			housegen.addExitDoors (w, world.getName());
    			movePlayerThroughDoor (w, door);
    			for (int i = 0; i < 5; i++){
    				w.addActor(CreatureFactory.createCreature("zombie1", w));
    				Gold g = new Gold(ColoredChar.create('o', Color.yellow),
        					"Gold");
        			w.addActor(g);
    			}
    			w.addActor(CreatureFactory.createCreature("hitler", w));

    		}
    		w.useViewfield(false);
    		worlds.put(door.getDestWorld(), w);
    	}
    	else
    	{
    		movePlayerThroughDoor(w, door);
    	}
    	world = w;
    }

    private static Generator getLevelGenerator()
    {
    	return new City();
//        return new Cellular();
    }
    
    private static House getHouseGenerator()
    {
    	return new House();
    }
}
