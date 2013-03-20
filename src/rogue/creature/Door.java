package rogue.creature;

import jade.util.datatype.ColoredChar;

import java.awt.Color;

import pazi.behaviour.DeadBehaviour;
import pazi.behaviour.DoNothingBehaviour;
import pazi.weapons.KnuckleDuster;

public class Door extends Monster {
	String text = "Mist, die Tür klemmt! Ich brauche etwas, um sie aufzubrechen!";
	public Door() {
		super(ColoredChar.create('═', new Color(0x663300)), "Tür");
		maxHp = 500;
		setHP(500);
		setBehaviour(DoNothingBehaviour.getInstance());
	}
	
	@Override
	public void takeDamage(int d, Creature source) {
		if(!source.isPlayer())
			return;
		Player player = (Player)source;
		if(player.meleeWeapon.getClass() != KnuckleDuster.class) 
			player.world().setMessage(text);
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
		return "KRACH!!!!";
	}
}
