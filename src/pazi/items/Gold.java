package pazi.items;


import java.awt.Color;

import pazi.core.Actor;
import pazi.creature.Creature;
import pazi.util.datatype.ColoredChar;


public class Gold extends Item {
	
	protected int amount;

	protected Gold() {
		this((int) (Math.random()*100));
	}
	
	protected Gold(int amount) {
		super(ColoredChar.create('o', Color.yellow), "Gold");
		this.amount = amount;
	}
	
	@Override
	public void getPickedUp (Creature creature){
		creature.appendMessage("Ich habe "+ amount +" Gold gefunden.", true);
		creature.getGold(amount);
		this.expire();
	}

	@Override
	public void interact(Actor actor) {}
	
}