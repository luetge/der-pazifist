package rogue.weapons;

import pazi.features.Paralyzed;
import rogue.creature.Creature;

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
			target.addGeneralFeature(new Paralyzed(2));
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

}
