package pazi.items;

import jade.core.Actor;
import jade.util.Dice;
import jade.util.datatype.ColoredChar;

import java.awt.Color;

import rogue.creature.Creature;
import rogue.creature.Player;

public class HealingPotion extends Item {

	private int HP;
	
	protected HealingPotion() {
		this(Dice.global.nextInt(40,60));
	}
	
	protected HealingPotion(int HP){
		super(new ColoredChar('T', Color.green), "Heiltrank (" + HP + " HP)");
		this.HP = HP;
		description = "Ein Heiltrank, der maximal " + HP + " HP wiederherstellt.";
	}

	@Override
	public void getPickedUp(Creature creature) {
		super.getPickedUp(creature);
		creature.appendMessage("Hallelujah! Ein Heiltrank, der mir " + HP + " HP beschert!");
	}

	@Override
	public void interact(Actor actor) {
		if(actor.isPlayer()){
			((Player)actor).addHP(HP);
			actor.appendMessage("Aaaaaah, das tut gut, ich fühle mich wie auferstanden!");
			((Player)actor).setHasActed(true);
		}
//		this.detach();
//		this.expire();
		// TODO: expire o.ä.
	}
}
