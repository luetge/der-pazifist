package pazi.weapons;

import jade.util.datatype.ColoredChar;

import java.awt.Color;

import rogue.creature.Creature;

public class SniperRifle extends RCWeaponPrototype {

	public SniperRifle(int minD, int maxD, double range, double prob, String name, Creature holder) {
		super(minD, maxD, range, prob, name, new ColoredChar('I', Color.cyan), holder, 20);
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
	
	
	@Override
	public void shoot(Creature attacker, Creature victim) {
		super.shoot(attacker, victim);
	}
	
	@Override
	protected String getMissedText(Creature attacker, Creature victim) {
		if (victim != null)
			return "...schießt meilenweit daneben. " + victim.getName() + " lacht ihn aus.";
		else
			return "";
	}
	
	@Override
	protected String getHitText(Creature attacker, Creature victim) {
		return "...trifft. Autsch!";
	}
	
	@Override
	public String getDescription() {
		return "Auto-aim Sniper. Range von " + this.range + ". Gegner muss > 1 Feld entfernt stehen.";
	}
}
