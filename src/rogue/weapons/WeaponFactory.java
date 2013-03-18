package rogue.weapons;

public class WeaponFactory {
	
	public static IWeapon createWeapon(String identifier){
		IWeapon weapon = null;
		if(identifier.equals("fist")) 
			weapon = new Fist();
		else if(identifier.equals("rottenFist"))
			weapon = new MeleeWeaponPrototype(5, 10, 0.9, "Verrottete Zombiefaust");
		else if(identifier.equals("sniper"))
			weapon = new RCWeaponPrototype(15, 30, 8, 0.4, "Sniper");
		return weapon;
	}

}
