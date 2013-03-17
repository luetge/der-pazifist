package rogue.weapons;

import rogue.creature.Creature;

public class Fist implements IMeleeWeapon {

	@Override
	public int getDamage(Creature creature) {
		return 30;
	}

	@Override
	public double getProb(Creature creature) {
		return 1;
	}

}
