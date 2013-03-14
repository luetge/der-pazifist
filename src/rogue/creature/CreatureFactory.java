package rogue.creature;

import jade.core.World;
import jade.core.Dialog;
import jade.util.datatype.ColoredChar;

import java.awt.Color;

import pazi.behaviour.DefaultFightBehaviour;
import pazi.behaviour.Follow;
import pazi.behaviour.ParalyzerBehaviour;
import pazi.behaviour.RandomBehaviour;
import pazi.behaviour.SneakStealFlee;
import pazi.features.Braaaiiiiins;
import pazi.features.EatBrains;
import rogue.behaviour.RandomWalk;

public class CreatureFactory {
	public static Creature createCreature(String identifier, World world){
		Creature creature = null;
		if(identifier.equals ("zombie1")){
			ColoredChar faces[] = new ColoredChar[9];
			for (int i = 0; i < 9; i++)
				faces[i] = ColoredChar.create('Z', new Color(0x00FF00+i));
			creature = new Monster(faces, "Blutiger Zombie");
			creature.addGeneralFeature(new Braaaiiiiins());
	        creature.setFightBehaviour(DefaultFightBehaviour.getInstance());
	        creature.getWalkFeatures().add(new EatBrains());
			creature.setWalkBehaviour(new Follow(world.getPlayer(), 5, 30));
			creature.setBehaviour(new RandomBehaviour());
		} else if (identifier.equals("bandit2")){
				creature = new Monster(ColoredChar.create(' ', Color.red), "Touchy Hobbit");
				//creature.addGeneralFeature(Braaaiiiiins.getInstance());
				ColoredChar faces[] = new ColoredChar[9];
				for (int i = 0; i < 9; i++)
					faces[i] = ColoredChar.create('B', new Color(0xFF0000+i));
				creature.setBehaviour(new SneakStealFlee(world.getPlayer(), faces));
		} else if (identifier.equals("alien1")){
			ColoredChar faces[] = new ColoredChar[9];
			for (int i = 0; i < 9; i++)
				faces[i] = ColoredChar.create('A', new Color(0xFFFF00+i));
			creature = new Monster(faces, "Schleimiges Alien");
			creature.setBehaviour(new ParalyzerBehaviour(world.getPlayer()));
		} else if (identifier.equals("priest")) {
			creature = new Ally (ColoredChar.create('P'), "Priest", new Dialog ());
		} else if (identifier.equals("hitler")) {
			creature = new Monster(ColoredChar.create('H', Color.white), "Hitler");
			creature.setFightBehaviour(DefaultFightBehaviour.getInstance());
			creature.setWalkBehaviour(new RandomWalk());
			creature.addGeneralFeature(new Braaaiiiiins("Arrrrrrr!", 10));
			creature.setBehaviour(new RandomBehaviour());
			creature.max_d = 100;
			creature.min_d = 30;
			creature.setHP(300);
	    } else {
			System.out.println("Konnte Kreatur \"" + identifier + "\" nicht laden!");
		}
		
		
		return creature;
	}
}