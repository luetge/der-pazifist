package pazi.weapons;

import rogue.creature.Creature;

public class RCWeaponPrototype extends WeaponPrototype implements IRangedCombatWeapon {

	public RCWeaponPrototype(int min_d, int max_d, double range, double prob, String name, Creature holder) {
		super(min_d, max_d, range, prob, name, holder);
	}
	
	public RCWeaponPrototype(int min_d, int max_d, double range, double prob, String name) {
		super(min_d, max_d, range, prob, name);
	}

	@Override
	public double getRange() {
		return range;
	}

}
