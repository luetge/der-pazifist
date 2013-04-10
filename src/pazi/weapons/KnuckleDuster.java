package pazi.weapons;


import java.awt.Color;

import pazi.creature.Creature;
import pazi.util.datatype.ColoredChar;



public class KnuckleDuster extends MeleeWeaponPrototype {

	public KnuckleDuster() {
		this(null);
	}
	
	public KnuckleDuster(Creature holder) {
		super(45, 80, 1, "Schlagring des Grauens", new ColoredChar('B', Color.magenta), holder);
		description = "Ein epischer Schlagring aus gehärtetem Stahl.";
	}
	
	/*protected static final int min_d = 100;
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
	}*/

}
