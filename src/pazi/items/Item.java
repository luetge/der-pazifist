package pazi.items;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;
import rogue.creature.Creature;

public abstract class Item extends Actor {
	
	protected String description = "Es ist leider keine Beschreibung vorhanden.";
	
	public Item(ColoredChar face, String name) {
		super(face, name);
		setPassable(true);
	}

	public void getPickedUp(Creature creature) {
		creature.world().removeActor(this);
		creature.getInventory().addItem(this);
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public String getDescription(){
		return description;
	}
	
}