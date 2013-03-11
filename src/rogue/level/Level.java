package rogue.level;

import jade.core.World;
import jade.core.Door;
import jade.gen.Generator;
import jade.gen.map.*;
import rogue.creature.Player;
import java.util.HashMap;
import java.util.Map;

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
    	World w = worlds.get(name);
    	if (w == null)
    	{
    		w = new World (128, 128, name);
    		roomgen.generate(w);
    		roomgen.addExit(w, new Door(startname, player.pos()));
    		worlds.put(name, w);
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
