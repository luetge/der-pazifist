package pazi.items;

import jade.core.Actor;
import jade.ui.Backpack;
import jade.ui.HUD;
import jade.util.Guard;
import jade.util.Lambda;

import java.util.ArrayList;

import rogue.creature.Creature;
import rogue.creature.Player;

public class Inventory {

	protected ArrayList<Item> items = new ArrayList<Item>();
	protected int gold = 0;
	protected Creature owner;
	
	public Inventory(Creature owner) {
		this.owner = owner;
		if(owner.isPlayer())
			Backpack.setInventory(this);
	}
	
	protected void update() {
		if(owner.isPlayer())
			Backpack.updateInventory();
	}
	
	public void addItem(Item item){
		items.add(item);
		update();
	}
	
	public boolean hasItem (String name, int amount)
	{
		// TODO: bessere Implementierung nötig
		if (name.equals("gold"))
			return getGold() >= amount;
		int count = 0;
		for (Item item : items)
		{
			if (item.getName().equals(name))
			{
				count++;
				if (count >= amount)
					return true;
			}
		}
		return false;
	}
	
	public void removeItem(Item item){
		Guard.verifyState(items.contains(item));
		items.remove(item);
		update();
	}
	
	public int removeItems(String id, int amount)
	{
		ArrayList<Item> items = new ArrayList<Item>();
		int removed = 0;
		for (Item item : this.items)
		{
			if (item.getIdentifier().equals(id) && removed < amount)
			{
				removed++;
			}
			else
			{
				items.add(item);
			}
		}
		this.items = items;
		update();
		return removed;
	}
	
	public Item getItem (String id)
	{
		for (Item item : items)
		{
			if (item.getIdentifier().equals(id))
				return item;
		}
		return null;
	}
	
	public int giveItem(Creature recipient, String name, int amount)
	{
		if (name.equals("gold"))
		{
			int realamount = loseGold(amount);
			recipient.getInventory().findGold(realamount);
			return realamount;
		}
		int realamount = 0;
		while (realamount < amount)
		{
			Item item = getItem (name);
			if (item == null)
				break;
			removeItem (item);
			recipient.getInventory().addItem(item);
			realamount++;
		}
		update(); 
		return realamount;
	}
	
	public ArrayList<Item> getItems(){
		return (ArrayList<Item>)items.clone();
	}
	
    public <T extends Item> T getItems(Class<T> cls)
    {
        return Lambda.first(Lambda.filterType(items, cls));
    }

	/**
	 * Addiert den angegebenen Betrag zum Gold dazu.
	 * @param gold
	 */
	public void findGold(int gold) {
		Guard.argumentIsNonNegative(gold);
		
		this.gold += gold;
		if(owner.isPlayer())
			HUD.setGold(this.gold);
		
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
		if(owner.isPlayer())
			HUD.setGold(this.gold);
		return gold;
	}

	public int getGold() {
		return gold;
	}

	public Actor getOwner() {
		return owner;
	}
}
