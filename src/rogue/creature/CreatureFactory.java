package rogue.creature;

import jade.core.World;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Direction;

import java.awt.Color;

import pazi.features.Braaaiiiiins;
import pazi.features.Follow;
import pazi.features.RandomBehaviour;

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
			creature.setWalkBehaviour(new Follow(world.getPlayer(), 20));
			creature.setBehaviour(new RandomBehaviour());
		} else {
			System.out.println("Konnte Kreatur \"" + identifier + "\" nicht laden!");
		}
		return creature;
	}
}