package pazi.weapons;

import pazi.creature.Creature;
import pazi.util.datatype.ColoredChar;

public class RCWeaponPrototype extends WeaponPrototype implements IRangedCombatWeapon {

	public RCWeaponPrototype(int min_d, int max_d, double range, double prob, String name, ColoredChar face, Creature holder, int ammo) {
		super(min_d, max_d, range, prob, name, face, holder, ammo);
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
