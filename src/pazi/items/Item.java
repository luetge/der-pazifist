package pazi.items;

import rogue.creature.Player;
import jade.core.Actor;
import jade.util.datatype.ColoredChar;

public abstract class Item extends Actor {
	

	public Item(ColoredChar face, String name) {
		super(face, name);
		setPassable(true);
	}
	
	public abstract void getPickedUp (Player player);
	
}