package pazi.weapons;

import rogue.creature.Creature;

public class Fist extends MeleeWeaponPrototype implements IMeleeWeapon {
	public Fist(Creature holder) { super(20, 60, 1, "Faust", holder); }
}
