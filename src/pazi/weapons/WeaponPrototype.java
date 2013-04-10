package pazi.weapons;

import pazi.core.Actor;
import pazi.core.Messenger;
import pazi.creature.Creature;
import pazi.creature.Player;
import pazi.items.Item;
import pazi.util.datatype.ColoredChar;

public class WeaponPrototype extends Item implements IWeapon {
	
	protected int min_d, max_d;
	protected double prob, range;
	protected String name;
	protected Creature holder;	// Wer hält diese Waffe?
	protected int ammoLeft;
	protected boolean melee = true;
	
	public WeaponPrototype(int min_d, int max_d, double range, double prob, String name, ColoredChar face, Creature holder, int ammo){
		super(face, name);
		this.min_d = min_d;
		this.max_d = max_d;
		this.prob = prob;
		this.range = range;
		this.name = name;
		setHolder(holder);
		ammoLeft = ammo;
	}

	public WeaponPrototype(int min_d, int max_d, double range, double prob, String name, ColoredChar face, Creature holder){
		this(min_d, max_d, range, prob, name, face, holder, -1);
	}
	
	public WeaponPrototype(int min_d, int max_d, double range, double prob, String name, ColoredChar face){
		this(min_d, max_d, range, prob, name, face, null);
	}

	@Override
	public int getDamage(Creature attacker, Creature victim) {
		return (int)(Math.random() * (max_d - min_d) + min_d);
	}

	@Override
	public double getProb(Creature attacker, Creature victim) {
		return ((attacker == null || victim == null || attacker.pos().distance(victim.pos()) > range) ? 0 : prob);
	}

	@Override
	public void shoot(Creature attacker, Creature victim) {
		if (ammoLeft > 0)
			reduceAmmo();
		if(attacker != null && victim != null)
			appendMessage(attacker.world(), getWeaponFiredText(attacker, victim));
			if(Math.random() < getProb(attacker, victim)){
				victim.takeDamage(getDamage(attacker, victim), attacker, melee);
				appendMessage(victim.world(), getHitText(attacker, victim));
			}
			else
				appendMessage(attacker.world(), getMissedText(attacker, victim));
	}
	
	protected void reduceAmmo() {
		ammoLeft -= 1;
		if (ammoLeft <= 0)
			this.expire();
		if (holder.getClass() == Player.class)
			((Player) holder).refreshWeaponsHUD();
	}

	/**
	 * if ammo is depleted, lose weapon.
	 */
	@Override
	public void expire() {
		super.expire();
		if(holder != null)
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
	public void interact(Actor actor) {
		if(actor.isPlayer()){
			((Creature)actor).setWeapon(this);
			appendMessage(actor, getEquipText());
			setHasActed(false);
		}
	}

	@Override
	public void getPickedUp(Creature creature) {
		super.getPickedUp(creature);
		appendMessage(creature, getPickupText());
	}
	
	protected String getPickupText() {
		return "";
	}
	
	protected String getMissedText(Creature attacker, Creature victim) {
		return "";
	}

	protected String getHitText(Creature attacker, Creature victim) {
		return "";
	}
	
	protected String getEquipText() {
		return "";
	}

	protected void appendMessage(Messenger messenger, String message) {
		if(message != null && !message.isEmpty())
			messenger.appendMessage(message);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getAmmo() {
		return ammoLeft;
	}


}
