package pazi.weapons;

import java.awt.Color;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;
import pazi.items.Item;
import rogue.creature.Creature;

public class Shotgun extends RCWeaponPrototype {

	public Shotgun() {
		
		super(new ColoredChar('p', Color.lightGray), "Shotgun der Erlösung");
		description = "Eine Waffe mit kurzer Reichweite und großer Streuung.";
	}

	@Override
	public int getDamage(Creature attacker, Creature victim) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getProb(Creature attacker, Creature victim) {
		return 1;
	}

	@Override
	public void shoot(Creature attacker, Creature victim) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getRange() {
		return 4;
	}

	@Override
	public void interact(Actor actor) {
		if(Creature.class.isAssignableFrom(actor.getClass())){
			((Creature)actor).setRCWeapon(this);
			actor.appendMessage("Die Welt gehört mir!!");
			setHasActed(false);
		}
	}
	
	@Override
	public void getPickedUp(Creature creature) {
		super.getPickedUp(creature);
		creature.appendMessage("Eine Shotgun, die hätte ich gegen die Römer damals gebraucht!");
	}

}
