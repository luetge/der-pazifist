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
			for (int i = 0; i < 9; i++)
				faces[i] = ColoredChar.create('Z', new Color(0x00FF00+i));
			creature = new Monster(faces, "Blutiger Zombie");
			creature.addGeneralFeature(Braaaiiiiins.getInstance());
	        creature.setFightBehaviour(new DefaultFightBehaviour());
	        creature.getWalkFeatures().add(new EatBrains());
			creature.setWalkBehaviour(new Follow(world.getPlayer(), 5));
			creature.setBehaviour(new RandomBehaviour());
		} else if (identifier == "bandit2"){
				creature = new Monster(ColoredChar.create(' ', Color.red), "Touchy Hobbit");
				creature.addGeneralFeature(Braaaiiiiins.getInstance());
				creature.setBehaviour(new SneakStealFlee(world.getPlayer(), 'B'));
		} else {
			System.out.println("Konnte Kreatur \"" + identifier + "\" nicht laden!");
		}
		
		
		return creature;
	}
}