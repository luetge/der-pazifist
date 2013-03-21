package pazi.weapons;

import jade.util.datatype.ColoredChar;
import rogue.creature.Creature;

public class RCWeaponPrototype extends WeaponPrototype implements IRangedCombatWeapon {

	public RCWeaponPrototype(int min_d, int max_d, double range, double prob, String name, ColoredChar face, Creature holder, int ammo) {
		super(min_d, max_d, range, prob, name, face, holder);
		melee = false;
	}
	
	public RCWeaponPrototype(int min_d, int max_d, double range, double prob, String name, ColoredChar face, int ammo) {
		this(min_d, max_d, range, prob, name, face, null, ammo);
	}
	
	@Override
	public double getRange() {
		return range;
	}
}
