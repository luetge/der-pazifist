package pazi.items;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;

import java.awt.Color;

import rogue.creature.Creature;

public class Gold extends Item {
	
	protected int amount;

	public Gold() {
		this((int) (Math.random()*100));
	}
	
	public Gold(int amount) {
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