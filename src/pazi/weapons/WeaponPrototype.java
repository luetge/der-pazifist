package pazi.weapons;

import rogue.creature.Creature;

public class WeaponPrototype implements IWeapon {
	
	protected int min_d, max_d;
	protected double prob, range;
	protected String name;
	protected String weaponFiredTemp = "", weaponMissed = "Mist, daneben!", weaponHit = "";
	protected Creature holder;	// Wer hält diese Waffe?
	protected int ammoLeft;
	
	public WeaponPrototype(int min_d, int max_d, double range, double prob, String name, Creature holder, int ammo){
		this.min_d = min_d;
		this.max_d = max_d;
		this.prob = prob;
		this.range = range;
		this.name = name;
		setHolder(holder);
		ammoLeft = ammo;
	}

	public WeaponPrototype(int min_d, int max_d, double range, double prob, String name, Creature holder){
		this(min_d, max_d, range, prob, name, holder, -1);
	}
	
	public WeaponPrototype(int min_d, int max_d, double range, double prob, String name){
		this(min_d, max_d, range, prob, name, null, -1);
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
		if (ammoLeft > 0)
			reduceAmmo();
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
	
	private void reduceAmmo() {
		ammoLeft -= 1;
		if (ammoLeft <= 0)
			this.expire();
	}

	/**
	 * if ammo is depleted, lose weapon.
	 */
	private void expire() {
		holder.expireWeapon(this);
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
