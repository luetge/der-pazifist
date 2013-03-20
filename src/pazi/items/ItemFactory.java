package pazi.items;

import java.awt.Color;

import pazi.weapons.Fist;
import pazi.weapons.IWeapon;
import pazi.weapons.KnuckleDuster;
import pazi.weapons.MeleeWeaponPrototype;
import pazi.weapons.Shotgun;
import pazi.weapons.SniperRifle;

import jade.util.datatype.ColoredChar;
import rogue.creature.Creature;

public class ItemFactory {
	
	public static IWeapon createWeapon(String identifier, Creature holder){
		IWeapon weapon = null;
		if(identifier.equals("fist")) 

			weapon = new Fist();
		else if(identifier.equals("rottenFist"))
			weapon = new MeleeWeaponPrototype(5, 10, 0.9, "Verrottete Zombiefaust", new ColoredChar(' ', Color.black), holder);
		else if(identifier.equals("sniper"))
			weapon = new SniperRifle(15, 30, 8, 0.4, "Scharfschützengewehr", holder);
		else if(identifier.equals("headnut"))
			weapon = new MeleeWeaponPrototype(1, 3, 0.5, "Kopfnuss der Verzweiflung", new ColoredChar(' ', Color.black), holder);
		else if(identifier.equals("knuckleduster"))
			weapon = new KnuckleDuster();
		else if(identifier.equals("shotgun"))
			weapon = new Shotgun();
		return weapon;
	}
	
	public static Item createItem(String identifier, Creature holder){
		
		
		
		return (Item)createWeapon (identifier, holder);
	}

	
	public static IWeapon createWeapon(String identifier){
		return createWeapon(identifier, null);
	}

}
