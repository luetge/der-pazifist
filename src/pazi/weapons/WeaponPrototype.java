package pazi.weapons;

import rogue.creature.Creature;

public class WeaponPrototype implements IWeapon {
	
	protected int min_d, max_d;
	protected double prob, range;
	protected String name;
	
	public WeaponPrototype(int min_d, int max_d, double range, double prob, String name){
		this.min_d = min_d;
		this.max_d = max_d;
		this.prob = prob;
		this.range = range;
		this.name = name;
	}

	@Override
	public int getDamage(Creature attacker, Creature victim) {
		return (int)(Math.random() * (max_d - min_d) + min_d);
	}

	@Override
	public double getProb(Creature attacker, Creature victim) {
		return attacker == null || victim == null || attacker.pos().distance(victim.pos()) > range ? 0 : prob;
	}

	@Override
	public void shoot(Creature attacker, Creature victim) {
		if(attacker != null && victim != null)
			if(Math.random() < getProb(attacker, victim)){
				attacker.world().appendMessage(attacker.getName() + " greift an mit \"" + getName() + "\".");
				victim.takeDamage(getDamage(attacker, victim));
			}
			else
				attacker.appendMessage("Mist, daneben!");

	}

	@Override
	public String getName() {
		return name;
	}

}
