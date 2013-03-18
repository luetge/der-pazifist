package pazi.weapons;

import rogue.creature.Creature;

public interface IWeapon {
	int getDamage(Creature attacker, Creature victim);
	double getProb(Creature attacker, Creature victim);
	void shoot(Creature attacker, Creature victim);
	String getName();
}
