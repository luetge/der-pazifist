package pazi.weapons;

import pazi.creature.Creature;
import pazi.features.Paralyzed;

public class Paralyzer implements IMeleeWeapon {

	int cooldown;
	int counter;
	Creature target;
	
	public Paralyzer(Creature target, int cooldown){
		this.cooldown = cooldown;
		this.target = target;
		counter = cooldown;
	}
	
	@Override
	public void shoot(Creature attacker, Creature victim) {
		if (counter < cooldown){
			counter++;
			return;
		}
		
		if(attacker.pos().distance(victim.pos()) < 2){
			target.addGeneralFeature(new Paralyzed(2, attacker, target));
			counter = 0;
			attacker.appendMessage("Freeze, Motherfucker!");
		}
	}
	
	
	
	@Override
	public int getDamage(Creature attacker, Creature victim) {
		return 0;
	}

	@Override
	public double getProb(Creature attacker, Creature victim) {
		return 1;
	}

	@Override
	public String getName() {
		return "Paralysator";
	}

	/*
	 * (non-Javadoc)
	 * returns remaining cooldown
	 */
	@Override
	public int getAmmo() {
		return (cooldown - counter);
	}

}
