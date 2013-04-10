package pazi.weapons;


import java.awt.Color;

import pazi.behaviour.IBehaviour;
import pazi.creature.Creature;
import pazi.util.datatype.ColoredChar;


public class Confuser extends MeleeWeaponPrototype {

	protected int cooldown = 40;
	protected int counter;
	
	public Confuser() {
		super(0, 0, 1, "Ohrfeige der Verwirrung", new ColoredChar(' ', Color.black), null);
		counter = 0;
	}
	
	@Override
	public void shoot(Creature attacker, Creature victim) {
		if(victim != null && counter == 0){
			victim.appendMessage("Wurde durch eine schallende Ohrfeige verwirrt!");
			
			IBehaviour victimOldBehaviour = victim.getBehaviour();
			if (victimOldBehaviour.getClass() == ConfusedBehaviour.class){
				ConfusedBehaviour victimOldConfusedB = (ConfusedBehaviour) victimOldBehaviour;
				victimOldConfusedB.setRounds(30);
			} else
				victim.setBehaviour(new ConfusedBehaviour(victim.getBehaviour(), 30, 0.33));
			if(attacker != null)
				attacker.setHasActed(true);
		}
		if(counter++ == cooldown)
			counter = 0;
	}
	
}
