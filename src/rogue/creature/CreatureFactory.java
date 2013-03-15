package rogue.creature;

import jade.core.World;
import jade.core.Dialog;
import jade.util.datatype.ColoredChar;

import java.awt.Color;

import pazi.behaviour.DefaultFightBehaviour;
import pazi.behaviour.DefaultRangedCombatBehaviour;
import pazi.behaviour.Follow;
import pazi.behaviour.ParalyzerBehaviour;
import pazi.behaviour.DefaultRandomBehaviour;
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
	        creature.setCloseCombatBehaviour(DefaultFightBehaviour.getInstance());
	        creature.getWalkFeatures().add(new EatBrains());
			creature.setWalkBehaviour(new Follow(world.getPlayer(), 8, 0.2));
			creature.setBehaviour(new DefaultRandomBehaviour());
		} else if (identifier.equals("bandit2")){
				creature = new Monster(ColoredChar.create(' ', Color.red), "Touchy Hobbit");
				//creature.addGeneralFeature(Braaaiiiiins.getInstance());
				ColoredChar faces[] = new ColoredChar[9];
				for (int i = 0; i < 9; i++)
					faces[i] = ColoredChar.create('B', new Color(0xFF0000+i));
				creature.setBehaviour(new SneakStealFlee(world.getPlayer(), faces));
				creature.getGold(5);
		} else if (identifier.equals("alien1")){
			ColoredChar faces[] = new ColoredChar[9];
			for (int i = 0; i < 9; i++)
				faces[i] = ColoredChar.create('A', new Color(0xFFFF00+i));
			creature = new Monster(faces, "Schleimiges Alien");
			creature.setBehaviour(new ParalyzerBehaviour(world.getPlayer(), 8));
	
		} else if (identifier.equals("sniper1")){
			ColoredChar faces[] = new ColoredChar[9];
			for (int i = 0; i < 9; i++)
				faces[i] = ColoredChar.create('ø', Color.ORANGE);
			creature = new Monster(faces, "Mr. Sniper");
			creature.setBehaviour(new DefaultRandomBehaviour(1));
			creature.setRangedCombatBehaviour(new DefaultRangedCombatBehaviour(5, 0.5, 20, 10));
			creature.setWalkBehaviour(new Follow(world.getPlayer(), 13, 5, 0));
			
		} else if (identifier.equals("priest")) {
			creature = new Ally (ColoredChar.create('P'), "Priest", new Dialog ("res/dialogs/priest.txt"));
		} else if (identifier.equals("hitler")) {
			creature = new Monster(ColoredChar.create('H', Color.white), "Hitler");
			creature.setCloseCombatBehaviour(DefaultFightBehaviour.getInstance());
			creature.setWalkBehaviour(new RandomWalk());
			creature.addGeneralFeature(new Braaaiiiiins("Arrrrrrr!", 10));
			creature.setBehaviour(new DefaultRandomBehaviour());
			creature.max_d = 100;
			creature.min_d = 30;
			creature.setHP(300);
	    } else {
			System.out.println("Konnte Kreatur \"" + identifier + "\" nicht laden!");
		}
		
		
		return creature;
	}
}