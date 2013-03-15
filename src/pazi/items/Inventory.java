package pazi.items;

import jade.core.Actor;
import jade.ui.Bagpack;
import jade.util.Guard;

import java.util.ArrayList;

import rogue.creature.Creature;
import rogue.creature.Player;

public class Inventory {

	protected ArrayList<Item> items = new ArrayList<Item>();
	protected int gold = 0;
	protected Creature owner;
	
	public Inventory(Creature owner) {
		this.owner = owner;
		if(Player.class.isAssignableFrom(owner.getClass()))
			Bagpack.setInventory(this);
	}
	
	protected void update() {
		if(Player.class.isAssignableFrom(owner.getClass()))
			Bagpack.updateInventory();
	}
	
	public void addItem(Item item){
		items.add(item);
		update();
	}
	
	public void removeItem(Item item){
		Guard.verifyState(items.contains(item));
		items.remove(item);
		update();
	}
	
	public ArrayList<Item> getItems(){
		return (ArrayList<Item>)items.clone();
	}

	/**
	 * Addiert den angegebenen Betrag zum Gold dazu.
	 * @param gold
	 */
	public void findGold(int gold) {
		Guard.argumentIsNonNegative(gold);
		
		this.gold += gold;
	}
	
	/**
	 * Inventar verliert Gold, wenn nicht genug vorhanden ist, wird die maximale Anzahl
	 * zurückgegeben.
	 * @param gold
	 * @return
	 */
	public int loseGold(int gold) {
		Guard.argumentIsNonNegative(gold);
		if(this.gold - gold < 0)
			gold = this.gold;
		this.gold -= gold;
		return gold;
	}

	public int getGold() {
		return gold;
	}

	public Actor getOwner() {
		return owner;
	}
}
