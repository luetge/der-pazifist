package pazi.items;

import rogue.creature.Player;
import jade.core.Actor;
import jade.util.datatype.ColoredChar;

public abstract class Item extends Actor {
	

	public Item(ColoredChar face, String name) {
		super(face, name);
		this.setPassable(true);
		// TODO Auto-generated constructor stub
	}
	
	public void getPickedUp (Player player){
	}
	
}