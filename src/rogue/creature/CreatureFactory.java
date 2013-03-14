package rogue.creature;

import jade.core.World;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Direction;

import java.awt.Color;

import pazi.behaviour.DefaultFightBehaviour;
import pazi.behaviour.Flee;
import pazi.behaviour.Follow;
import pazi.behaviour.RandomBehaviour;
import pazi.behaviour.SneakStealFlee;
import pazi.features.Braaaiiiiins;
import pazi.features.EatBrains;

public class CreatureFactory {
	public static Creature createCreature(String identifier, World world){
		Creature creature = null;
		if(identifier == "zombie1"){
			ColoredChar faces[] = new ColoredChar[9];
			faces[Direction.ORIGIN.getID()] = ColoredChar.create('Z', new Color(0x00FF00));
			faces[Direction.NORTH.getID()] = ColoredChar.create('Z', new Color(0x01FF00));
			faces[Direction.EAST.getID()] = ColoredChar.create('Z', new Color(0x02FF00));
			faces[Direction.SOUTH.getID()] = ColoredChar.create('Z', new Color(0x03FF00));
			faces[Direction.WEST.getID()] = ColoredChar.create('Z', new Color(0x04FF00));
			faces[Direction.NORTHEAST.getID()] = ColoredChar.create('Z', new Color(0x05FF00));
			faces[Direction.SOUTHEAST.getID()] = ColoredChar.create('Z', new Color(0x06FF00));
			faces[Direction.SOUTHWEST.getID()] = ColoredChar.create('Z', new Color(0x07FF00));
			faces[Direction.NORTHWEST.getID()] = ColoredChar.create('Z', new Color(0x08FF00));
			creature = new Monster(faces, "Blutiger Zombie");
			creature.addGeneralFeature(Braaaiiiiins.getInstance());
	        creature.setFightBehaviour(new DefaultFightBehaviour());
	        creature.getWalkFeatures().add(new EatBrains());
			creature.setWalkBehaviour(new Follow(world.getPlayer(), 5));
			creature.setBehaviour(new RandomBehaviour());
		} else if (identifier == "bandit2"){
				creature = new Monster(ColoredChar.create('.', Color.red), "Touchy Hobbit");
				creature.addGeneralFeature(Braaaiiiiins.getInstance());
				creature.setBehaviour(new SneakStealFlee(world.getPlayer(), 'B'));
//				creature.setWalkBehaviour(new Follow(world.getPlayer(),5));
		}
		
		{
			System.out.println("Konnte Kreatur \"" + identifier + "\" nicht laden!");
		}
		
		
		return creature;
	}
}