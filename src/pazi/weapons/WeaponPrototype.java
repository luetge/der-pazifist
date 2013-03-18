package pazi.weapons;

import rogue.creature.Creature;

public class WeaponPrototype implements IWeapon {
	
	protected int min_d, max_d;
	protected double prob, range;
	protected String name;
	protected String weaponFiredTemp = "", weaponMissed = "Mist, daneben!", weaponHit = "";
	Creature holder;	// Wer hält diese Waffe?
	
	public WeaponPrototype(int min_d, int max_d, double range, double prob, String name, Creature creature){
		this.min_d = min_d;
		this.max_d = max_d;
		this.prob = prob;
		this.range = range;
		this.name = name;
		setHolder(creature);
	}
	
	public WeaponPrototype(int min_d, int max_d, double range, double prob, String name){
		this.min_d = min_d;
		this.max_d = max_d;
		this.prob = prob;
		this.range = range;
		this.name = name;
		setHolder(null);
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
			weaponFiredTemp = getWeaponFiredText(attacker, victim);
			if (weaponFiredTemp != "")
				attacker.world().appendMessage(weaponFiredTemp);
			if(Math.random() < getProb(attacker, victim)){
				victim.takeDamage(getDamage(attacker, victim), attacker);
				if (weaponHit != "")
					victim.world().appendMessage(weaponHit);
			}
			else
				if (weaponMissed != "")
					attacker.world().appendMessage(weaponMissed);
	}
	
	public void setHolder(Creature creature){
		holder = creature;
	}
	
	public Creature getHolder(){
		return holder;
	}
	
	protected String getWeaponFiredText(Creature attacker, Creature victim){
		return attacker.getName() + " greift an mit \"" + this.getName() + "\".";
	}
	

	@Override
	public String getName() {
		return name;
	}


}
