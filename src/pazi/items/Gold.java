package pazi.items;

import rogue.creature.Player;
import jade.core.Actor;
import jade.util.datatype.ColoredChar;

public class Gold extends Item {
	
	protected int amount;

	public Gold(ColoredChar face, String name) {
		super(face, name);
		amount=(int) (Math.random()*100);
		// TODO Auto-generated constructor stub
	}
	
	public void getPickedUp (Player player){
		player.appendMessage("Ich habe "+ amount +" Gold gefunden.");
		player.getGold(amount);
		this.expire();
	}

	@Override
	public void interact(Actor actor) {}
	
}