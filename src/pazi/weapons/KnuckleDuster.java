package pazi.weapons;

import java.awt.Color;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;
import pazi.items.Item;
import pazi.weapons.IMeleeWeapon;
import rogue.creature.Creature;


public class KnuckleDuster extends Item implements IMeleeWeapon {

	protected static final int min_d = 100;
	protected static final int max_d = 200;
	
	public KnuckleDuster() {
		super(new ColoredChar('B', Color.magenta), "Schlagring des Grauens");
		description = "Ein epischer Schlagring aus gehärtetem Stahl.";
	}
	
	@Override
	public int getDamage(Creature attacker, Creature victim) {
		return (int)(Math.random() * (max_d - min_d) + min_d);
	}

	@Override
	public double getProb(Creature attacker, Creature victim) {
		return 1;
	}

	@Override
	public void shoot(Creature attacker, Creature victim) {
		if(attacker == null || victim == null)
			return;
		attacker.world().appendMessage(attacker.getName() + " greift an mit \"" + getName() + "\".");
		victim.takeDamage(getDamage(attacker, victim), attacker);
	}

	@Override
	public void interact(Actor actor) {
		if(Creature.class.isAssignableFrom(actor.getClass())){
			((Creature)actor).setMeleeWeapon(this);
			actor.appendMessage("Endlich kann ich ihn den benutzen, den Helfer der schwachen Fäuste!");
			setHasActed(false);
		}
	}
	
	@Override
	public void getPickedUp(Creature creature) {
		super.getPickedUp(creature);
		creature.appendMessage("Der Schlagring den mir meine Jünger zum Namenstag geschenkt haben, er fühlt sich an wie neu!");
	}

}
