package pazi.weapons;

import pazi.creature.Creature;
import pazi.util.datatype.ColoredChar;


public class MeleeWeaponPrototype extends WeaponPrototype implements IMeleeWeapon {

	public MeleeWeaponPrototype(int min_d, int max_d, double prob, String name, ColoredChar face, Creature holder, int duration){
		super(min_d, max_d, 2, prob, name, face, holder, duration);
	}
	
	public MeleeWeaponPrototype(int min_d, int max_d, double prob, String name, ColoredChar face, Creature holder){
		super(min_d, max_d, 2, prob, name, face, holder, -1);
	}
	
}
