package pazi.creature;

import pazi.behaviour.DeadBehaviour;
import pazi.behaviour.DoNothingBehaviour;
import pazi.util.datatype.ColoredChar;

public class DestructableObject extends Creature {
	
	String wrongWeaponText = "";
	String deathText = "";
	Class destructibleBy;
		
	public DestructableObject(ColoredChar face, String name, int hp, Class destructibleBy) {
		this(face, name, hp, destructibleBy, "", "");
	}
	
	public DestructableObject(ColoredChar face, String name, int hp, Class destructibleBy, String wrongWeaponText, String deathText) {
			super(face, name);
			setHP(hp);
			this.destructibleBy = destructibleBy;
			setBehaviour(DoNothingBehaviour.getInstance());
			this.wrongWeaponText = wrongWeaponText;
			this.deathText = deathText;
		}
		
	@Override
	public void takeDamage(int d, Creature source, boolean melee) {
		if(!source.isPlayer())
			return;
		Player player = (Player)source;
		if( (melee && !destructibleBy.isAssignableFrom(player.meleeWeapon.getClass())) || (!melee && (player.rcWeapon == null || !destructibleBy.isAssignableFrom(player.rcWeapon.getClass())))) 
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