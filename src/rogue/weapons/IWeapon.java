package rogue.weapons;

import rogue.creature.Creature;

public interface IWeapon {
	int getDamage(Creature creature);
	double getProb(Creature creature);
}
