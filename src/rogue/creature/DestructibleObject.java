package rogue.creature;

import jade.util.datatype.ColoredChar;
import pazi.behaviour.DeadBehaviour;
import pazi.behaviour.DoNothingBehaviour;

public class DestructibleObject extends Creature {
	
	String wrongWeaponText = "";
	String deathText = "";
	Class destructibleBy;
		
	public DestructibleObject(ColoredChar face, String name, int hp, Class destructibleBy) {
		this(face, name, hp, destructibleBy, "", "");
	}
	
	public DestructibleObject(ColoredChar face, String name, int hp, Class destructibleBy, String wrongWeaponText, String deathText) {
			super(face, name);
			System.out.println("Hallo");
			setHP(hp);
			this.destructibleBy = destructibleBy;
			setBehaviour(DoNothingBehaviour.getInstance());
			this.wrongWeaponText = wrongWeaponText;
			this.deathText = deathText;
		}
		
	@Override
	public void takeDamage(int d, Creature source) {
		if(!source.isPlayer())
			return;
		Player player = (Player)source;
		if(!destructibleBy.isAssignableFrom(player.meleeWeapon.getClass())) 
			player.world().setMessage(getWrongWeaponText());
		else {
			if(getBehaviour().getClass() == DeadBehaviour.class)
	    		return;
	    	setHP(Math.max(0, hp-d));
	    	if(hp == 0)
	    		setBehaviour(new DeadBehaviour(this,source));
		}
	}
		
	@Override
	public String getDeathMessage() {
		return deathText;
	}
	
	protected String getWrongWeaponText() {
		return wrongWeaponText;
	}
}