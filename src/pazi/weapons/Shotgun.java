package pazi.weapons;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;
import pazi.items.Item;
import rogue.creature.Creature;

public class Shotgun extends Item implements IRangedCombatWeapon {

	public Shotgun(ColoredChar face, String name) {
		super(face, name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getDamage(Creature attacker, Creature victim) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getProb(Creature attacker, Creature victim) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void shoot(Creature attacker, Creature victim) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getRange() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void interact(Actor actor) {
		// TODO Auto-generated method stub

	}

}