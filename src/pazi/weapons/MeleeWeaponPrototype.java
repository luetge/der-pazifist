package pazi.weapons;

import rogue.creature.Creature;


public class MeleeWeaponPrototype extends WeaponPrototype implements IMeleeWeapon {

	public MeleeWeaponPrototype(int min_d, int max_d, double prob, String name, Creature holder){
		super(min_d, max_d, 2, prob, name, holder);
	}
}
