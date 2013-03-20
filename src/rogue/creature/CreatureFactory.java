package rogue.creature;

import jade.core.Dialog;
import jade.core.World;
import jade.util.datatype.ColoredChar;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.newdawn.slick.util.ResourceLoader;

import pazi.behaviour.DefaultBehaviour;
import pazi.behaviour.Follow;
import pazi.behaviour.IBehaviour;
import pazi.behaviour.SneakStealFlee;
import pazi.features.Braaaiiiiins;
import pazi.features.IBeforeAfterFeature;
import pazi.features.IFeature;
import pazi.weapons.IMeleeWeapon;
import pazi.weapons.IRangedCombatWeapon;
import pazi.weapons.Paralyzer;
import pazi.weapons.WeaponFactory;
import rogue.behaviour.RandomWalk;

public class CreatureFactory {
	protected static HashMap<String, String> monsters = null;
	
	/**
	 * Erzeugt eine Kreatur per Identifier
	 * @param identifier
	 * @param world
	 * @return
	 */
	public static Creature createCreature(String identifier, World world){
		if(monsters == null)
			init();
		Creature creature = null;
		if(identifier.equals ("zombie2")){
//			creature = getCreatureFromString("Neuer Zombie;Z;0x00FF00;100;35;45;DefaultBehaviour;Follow(¥Player,8,0.2);Braaaiiiiins;rottenFist;;EatBrains;", world);
//			ColoredChar faces[] = new ColoredChar[9];
//			for (int i = 0; i < 9; i++)
//				faces[i] = ColoredChar.create('Z', new Color(0x00FF00+i));
//			creature = new Monster(faces, "Blutiger Zombie");
//			creature.addGeneralFeature(new Braaaiiiiins());
//	        creature.getWalkFeatures().add(new EatBrains());
//			creature.setWalkBehaviour(new Follow(world.getPlayer(), 8, 0.2));
//			creature.setBehaviour(new DefaultBehaviour());
//			creature.meleeWeapon = (IMeleeWeapon) WeaponFactory.createWeapon("rottenFist");
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
			creature.meleeWeapon = new Paralyzer(world.getPlayer(), 10);	
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
	    	creature = getCreatureFromString(monsters.get(identifier), world);
			if(creature == null)
				System.out.println("Konnte Kreatur \"" + identifier + "\" nicht laden!");
		}
		creature.setIdentifier(identifier);
		return creature;
	}
	
	private static void init() {
		BufferedReader reader;
		monsters = new HashMap<String, String>();
		try {
			reader = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceAsStream("res/monsters")));
			while(reader.ready())
				addMonster(reader.readLine());
			reader.close();
		} catch (IOException e) { }
	}
	
	private static void addMonster(String readLine) {
		if(readLine == null || !readLine.contains(";") || readLine.startsWith("//"))
			return;
		monsters.put(readLine.substring(0, readLine.indexOf(';')), readLine.substring(readLine.indexOf(';') + 1));
	}

	/**
	 * Kreatur aus Text laden, das Format hierbei ist
	 * Name;Zeichen;Farbe;HP;MIN_DAMAGE;MAX_DAMAGE;Behaviour;WalkingBehaviour;Features(Bindestrich separiert,Argumente in Klammern);MeleeWeapon;RCWeapon;WalkFeatures;FightFeatures
	 * @param text
	 * @param world
	 * @return
	 */
	private static Creature getCreatureFromString(String text, World world) {
		//Identifier;Name;Zeichen;Farbe;HP;MIN_DAMAGE;MAX_DAMAGE;Behaviour;WalkingBehaviour;Features(Bindestrich separiert,Argumente in Klammern);MeleeWeapon;RCWeapon;WalkFeatures;FightFeatures
		try{
			if(text == null || !text.contains(";"))
				return null;
			while(text.contains(";;"))
				text = text.replace(";;", "; ;");
			text = text + " ";
			String[] lst = text.split(";");
			for(int i=0; i<lst.length; i++)
				lst[i] = lst[i].trim();
			Creature creature = new Monster(new ColoredChar(lst[1].charAt(0), new Color(Integer.decode(lst[2]))), lst[0]);
			creature.setHP(Integer.parseInt(lst[3]));
			creature.min_d = Integer.parseInt(lst[4]);
			creature.max_d = Integer.parseInt(lst[5]);
			creature.setBehaviour(CreatureFactory.<IBehaviour>loadClass("pazi.behaviour." + lst[6], world));
			creature.setWalkBehaviour(CreatureFactory.<IBehaviour>loadClass("pazi.behaviour." + lst[7], world));
			for(String sFeature : lst[8].split("-"))
				creature.addGeneralFeature(CreatureFactory.<IFeature>loadClass("pazi.features." + sFeature, world));
			if(!lst[9].isEmpty())
				creature.meleeWeapon = (IMeleeWeapon) WeaponFactory.createWeapon(lst[9], creature);
			if(!lst[10].isEmpty())
				creature.rcWeapon = (IRangedCombatWeapon) WeaponFactory.createWeapon(lst[10], creature);
			for(String sFeature : lst[11].split("-"))
				addToList(creature.getWalkFeatures(), CreatureFactory.<IBeforeAfterFeature>loadClass("pazi.features." + sFeature, world));
			for(String sFeature : lst[12].split("-"))
				addToList(creature.getFightFeatures(), CreatureFactory.<IBeforeAfterFeature>loadClass("pazi.features." + sFeature, world));
				
			return creature;
		} catch (Exception ex) {
			System.out.println("Fehler beim Laden des Monsters " + text + ".");
		}
		return null;
	}
	
	protected static <T> void addToList(List<T> lst, T object){
		if(object != null && lst != null)
			lst.add(object);
	}
	
	protected static final HashMap<Class<?>, Class<?>> wrapper = new HashMap<Class<?>, Class<?>>(){ {
		put(int.class, Integer.class); 
		put(double.class, Double.class); 
		put(boolean.class, Boolean.class); 
		put(byte.class, Byte.class); 
		put(char.class, Character.class); 
		put(float.class, Float.class); 
		put(long.class, Long.class); 
		put(short.class, Short.class); 
		put(void.class, Void.class); 
	}};
	
	protected static Class<?> getWrapped(Class c){
		return c.isPrimitive() ? wrapper.get(c) : c;
	}
	
	/**
	 * Lade Klasse per Text
	 * @param cls
	 * @return
	 */
	private static <T> T loadClass(String cls, World world) {
		try {
			String argumente = "";
			if(cls.indexOf('(') != -1) {
				argumente = cls.substring(cls.indexOf('(') + 1, cls.length() - 1);
				cls = cls.substring(0, cls.indexOf('('));
			}
			
			// Keine Parameter
			if(argumente.isEmpty())
				return (T)ClassLoader.getSystemClassLoader().loadClass(cls).newInstance();
			
			// Mit Parametern
			Constructor<?>[] constructors = ClassLoader.getSystemClassLoader().loadClass(cls).getConstructors();
			String[] sArgs = argumente.split(",");
			for(Constructor<?> constructor : constructors){
				try {
					if(constructor.getParameterTypes().length == sArgs.length){
						ArrayList<Object> parameter = new ArrayList<Object>();
						Class<?>[] classes = constructor.getParameterTypes();
						for(int i=0; i<classes.length; i++)
							if(sArgs[i].equals("¥Player"))
								parameter.add(world.getPlayer());
							else if(sArgs[i].startsWith("¥"))
								parameter.add(World.class.getDeclaredField(sArgs[i].substring(1)));
							else
								parameter.add(getWrapped(classes[i]).getConstructor(new Class<?>[] {String.class}).newInstance(sArgs[i]));
						return (T)constructor.newInstance(parameter.toArray());
					}
				} catch (Exception e) {	}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}