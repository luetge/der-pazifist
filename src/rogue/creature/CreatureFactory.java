package rogue.creature;

import jade.core.World;
import jade.util.datatype.ColoredChar;

import java.awt.Color;

import pazi.behaviour.Follow;
import pazi.behaviour.RandomBehaviour;
import pazi.features.Braaaiiiiins;

public class CreatureFactory {
	public static Creature createCreature(String identifier, World world){
		Creature creature = null;
		if(identifier == "zombie1"){
			creature = new Monster(ColoredChar.create('Z', Color.green), "Blutiger Zombie");
			creature.addGeneralFeature(Braaaiiiiins.getInstance());
			creature.setWalkBehaviour(new Follow(world.getPlayer(), 20));
			creature.setBehaviour(new RandomBehaviour());
		} else {
			System.out.println("Konnte Kreatur \"" + identifier + "\" nicht laden!");
		}
		
		
		return creature;
	}
}