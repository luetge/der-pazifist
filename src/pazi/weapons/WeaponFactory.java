package pazi.weapons;

import rogue.creature.Creature;

public class WeaponFactory {
	
	public static IWeapon createWeapon(String identifier, Creature holder){
		IWeapon weapon = null;
		if(identifier.equals("fist")) 

			weapon = new Fist(holder);
		else if(identifier.equals("rottenFist"))
			weapon = new MeleeWeaponPrototype(5, 10, 0.9, "Verrottete Zombiefaust", holder);
		else if(identifier.equals("sniper"))
			weapon = new SniperRifle(15, 30, 8, 0.4, "Scharfschützengewehr", holder);
		else if(identifier.equals("headnut"))
			weapon = new MeleeWeaponPrototype(1, 3, 0.5, "Kopfnuss der Verzweiflung", holder);
		else if(identifier.equals("knuckleduster"))
			weapon = new KnuckleDuster();
		return weapon;
	}
	
	public static IWeapon createWeapon(String identifier){
		return createWeapon(identifier, null);
	}

}
