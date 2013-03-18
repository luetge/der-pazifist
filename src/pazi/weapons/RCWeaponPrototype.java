package pazi.weapons;

public class RCWeaponPrototype extends WeaponPrototype implements IRangedCombatWeapon {

	public RCWeaponPrototype(int min_d, int max_d, double range, double prob, String name) {
		super(min_d, max_d, range, prob, name);
	}

	@Override
	public double getRange() {
		return range;
	}

}
