package pazi.weapons;

import rogue.creature.Creature;

public class SniperRifle extends RCWeaponPrototype {

	
	public SniperRifle(int minD, int maxD, double range, double prob,
			String name, Creature holder) {
		super(minD, maxD, range, prob, name, holder, 20);
		weaponMissed = "...schießt meilenweit daneben. Du lachst ihn aus.";
		weaponHit = "...trifft. Autsch!";
		setHolder(holder);
	}
	
	@Override
	protected String getWeaponFiredText(Creature attacker, Creature victim){
		return attacker.getName() + " legt an, zielt auf \"" + victim.getName() + "\" und...";
	}
	
	/**
	 * 		Attention: Sniper has minimum Range of 2!
	 */
	@Override
	public double getProb(Creature attacker, Creature victim) {
		return attacker == null || victim == null || attacker.pos().distance(victim.pos()) < 2 || attacker.pos().distance(victim.pos()) > range ? 0 : prob;
	}

	
	
	
	
	

}
