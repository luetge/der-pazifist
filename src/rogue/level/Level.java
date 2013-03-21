package rogue.level;

import jade.core.Dialog;
import jade.core.Sequences;
import jade.core.World;
import jade.gen.Generator;
import jade.gen.map.AsciiMap;
import jade.gen.map.City;
import jade.gen.map.House;
import jade.ui.View;
import jade.util.Dice;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Door;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.util.ResourceLoader;

import pazi.behaviour.SequenceBehaviour;
import pazi.items.Item;
import pazi.items.ItemFactory;
import pazi.trigger.CreatureTrigger;
import pazi.trigger.ICreatureEvent;
import rogue.creature.Creature;
import rogue.creature.CreatureFactory;
import rogue.creature.Monster;
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
		if (toworld.getName().equals("mainworld"))
			View.get().setCenter(player.pos());
		else
			View.get().setCenter(toworld.width()/2, toworld.height()/2);
    }
    
    public void stepThroughDoor (Door door)
    {
    	World w = worlds.get(door.getDestWorld());
    	if (w == null)
    	{
    		if (ResourceLoader.resourceExists("res/rooms/"+door.getDestWorld()))
    		{
    			AsciiMap asciimap = new AsciiMap("res/rooms/"+door.getDestWorld());
        		w = new World (asciimap.width(), asciimap.height(), door.getDestWorld());
    			asciimap.render(w, 0, 0);
    			Map<Coordinate, Door> doors = asciimap.getDoors();
    			for (Coordinate coord : doors.keySet())
    			{
    				Door d = doors.get(coord);
    				d = new Door(d.getID(), d.getPosition(), d.getDestWorld(),
    						d.getDestID(), d.getDirection());
    				w.addDoor(d.getPosition(), d);
    			}
    			movePlayerThroughDoor (w, door);
    			asciimap.addCreatures(w);
    			asciimap.addTriggers(w);
    			asciimap.addItems(w);
    		}
    		else
    		{
        		int size;
    			if (door.getDestWorld().equals("bunker"))
    			{
    				size = 40;
    			}
    			else
    			{
    				size = Dice.global.nextInt(25, 30);
    			}

        		w = new World (size, size*3/4, door.getDestWorld());
    			housegen.generate(w);
    			housegen.addExitDoors (w, world.getName());
    			movePlayerThroughDoor (w, door);
    			if (w.getName().equals("bunker"))
    			{
    				w.addActor(CreatureFactory.createCreature("hitler", w));
    			}
    			else
    			{
    				if (Dice.global.chance(5))
    				{
    					w.addActor(CreatureFactory.createCreature("jokesteller", w));
    				}
    				else {
    				for (int i = 0; i < 8; i++)
    				{
    					if (Dice.global.chance(30))
    						continue;
    					
    					if (Dice.global.chance(80))
    						w.addActor(CreatureFactory.createCreature("zombie1", w));
    					else
    						w.addActor(CreatureFactory.createCreature("alien1", w));
    				}
    				}

    				for (int i = 0; i < 8; i++){
    					if (Dice.global.chance(70))
    						continue;
        				switch(Dice.global.nextInt(12))
        				{
        				case 0:
        				case 1:
        				case 2:
        				case 3:
        					w.addActor(ItemFactory.createItem("healingpotion"));
        					break;
        				case 4:
        				case 5:
        				case 6:
        					w.addActor(ItemFactory.createItem("gold"));
        					break;
        				case 7:
        					w.addActor(ItemFactory.createItem("knuckleduster"));
        					break;
        				case 8:
        					w.addActor(ItemFactory.createItem("shotgun"));
        					break;
        				case 9:
        					w.addActor(ItemFactory.createItem("frostsword"));
        					break;
        				case 10:
        					w.addActor(ItemFactory.createItem("vampsword"));
        					break;
        				case 11:
        					w.addActor(ItemFactory.createItem("sniper"));
        					break;
        				}
    				}
    			}
    		}
    		w.useViewfield(false);
    		worlds.put(door.getDestWorld(), w);
    	}
    	else
    	{
    		movePlayerThroughDoor(w, door);
    	}
    	world = w;
    	if (door.getID().startsWith("tut3exit"))
    	{
    		world.setActiveDialog(new Dialog("res/dialogs/tutexit.txt"));
    		world.getPlayer().setMeleeWeapon(null);
    		world.getPlayer().setRCWeapon(null);
    	}
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
