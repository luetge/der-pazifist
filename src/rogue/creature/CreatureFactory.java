package rogue.creature;

import jade.core.Dialog;
import jade.core.World;
import jade.util.datatype.ColoredChar;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import pazi.behaviour.DefaultBehaviour;
import pazi.behaviour.Follow;
import pazi.behaviour.IBehaviour;
import pazi.behaviour.SneakStealFlee;
import pazi.features.Braaaiiiiins;
import pazi.features.EatBrains;
import pazi.features.IFeature;
import pazi.weapons.IMeleeWeapon;
import pazi.weapons.IRangedCombatWeapon;
import pazi.weapons.Paralyzer;
import pazi.weapons.WeaponFactory;
import rogue.behaviour.RandomWalk;

public class CreatureFactory {
	protected static HashMap<String, String> monsters = null;
	public static Creature createCreature(String identifier, World world){
		if(monsters == null)
			init();
		Creature creature = null;
		if(identifier.equals ("zombie1")){
//			creature = getCreatureFromString("Neuer Zombie;Z;0x00FF00;100;35;45;DefaultBehaviour;Follow;Braaaiiiiins");
			ColoredChar faces[] = new ColoredChar[9];
			for (int i = 0; i < 9; i++)
				faces[i] = ColoredChar.create('Z', new Color(0x00FF00+i));
			creature = new Monster(faces, "Blutiger Zombie");
			creature.addGeneralFeature(new Braaaiiiiins());
	        creature.getWalkFeatures().add(new EatBrains());
			creature.setWalkBehaviour(new Follow(world.getPlayer(), 8, 0.2));
			creature.setBehaviour(new DefaultBehaviour());
			creature.meleeWeapon = (IMeleeWeapon) WeaponFactory.createWeapon("rottenFist");
		} else if (identifier.equals("bandit2")){
				creature = new Monster(ColoredChar.create(' ', Color.red), "El Bandido");
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
			creature.setWalkBehaviour(new Follow(world.getPlayer(), 8, 0.5));
			creature.meleeWeapon = new Paralyzer(world.getPlayer(), 20);	
			creature.setBehaviour(new DefaultBehaviour());
		} else if (identifier.equals("sniper1")){
			ColoredChar faces[] = new ColoredChar[9];
			for (int i : new int[]{0,2,3,7,8})
				faces[i] = ColoredChar.create('ø', new Color(0xFFFFFE));
			for (int i : new int[]{1,4,5,6})
				faces[i] = ColoredChar.create('ø', new Color(0xFFFFFF));
			creature = new Monster(faces, "Mr. Sniper");
			creature.setBehaviour(new DefaultBehaviour());
			creature.setWalkBehaviour(new Follow(world.getPlayer(), 13, 5, 0));
			creature.rcWeapon = (IRangedCombatWeapon) WeaponFactory.createWeapon("sniper", creature);
		} else if (identifier.equals("priest")) {
			creature = new Ally (ColoredChar.create('P'), "Priest", new Dialog ("res/dialogs/priest.txt"));
			
		} else if (identifier.equals("scientist")) {
			creature = new Ally (ColoredChar.create('W'), "Scientist", new Dialog ("res/dialogs/scientist.txt"));
		}	
		else if (identifier.equals("hitler")) {
			creature = new Monster(ColoredChar.create('H', Color.white), "Hitler");
			creature.setWalkBehaviour(new RandomWalk());
			creature.addGeneralFeature(new Braaaiiiiins("Arrrrrrr!", 10));
			creature.setBehaviour(new DefaultBehaviour());
			creature.max_d = 100;
			creature.min_d = 30;
			creature.setHP(300);
	    } else {
	    	creature = getCreatureFromString(monsters.get(identifier));
			System.out.println("Konnte Kreatur \"" + identifier + "\" nicht laden!");
		}
		return creature;
	}
	
	private static void init() {
		BufferedReader reader;
		monsters = new HashMap<String, String>();
		try {
			reader = new BufferedReader(new FileReader(new File("res/monsters")));
			while(reader.ready())
				addMonster(reader.readLine());
			reader.close();
		} catch (IOException e) { }
	}
	
	private static void addMonster(String readLine) {
		if(readLine == null || !readLine.contains(";") || readLine.startsWith("//"))
			return;
		monsters.put(readLine.substring(0, readLine.indexOf(';')), readLine);
	}

	private static Creature getCreatureFromString(String text) {
		// Argumente funktionieren noch nicht
		//Name;Zeichen;Farbe;HP;MIN_DAMAGE;MAX_DAMAGE;Behaviour;WalkingBehaviour;Features(Komma separiert,Argumente in Klammern)
		if(text == null || !text.contains(";"))
			return null;
		String[] lst = text.split(";");
		Creature creature = new Monster(new ColoredChar(lst[1].charAt(0), new Color(Integer.decode(lst[2]))), lst[0]);
		creature.setHP(Integer.parseInt(lst[3]));
		creature.min_d = Integer.parseInt(lst[4]);
		creature.max_d = Integer.parseInt(lst[5]);
		creature.setBehaviour(CreatureFactory.<IBehaviour>loadClass("pazi.behaviour." + lst[6]));
		creature.setWalkBehaviour(CreatureFactory.<IBehaviour>loadClass("pazi.behaviour." + lst[7]));
		for(String sFeature : lst[8].split(","))
			creature.addGeneralFeature(CreatureFactory.<IFeature>loadClass("pazi.features." + sFeature));
		return creature;
	}
	
	private static <T> T loadClass(String name) {
		try {
			return (T)ClassLoader.getSystemClassLoader().loadClass(name).newInstance();
		} catch (Exception e) {
			return null;
		}
	}
}