package pazi.items;

import jade.core.Actor;
import jade.util.Dice;
import jade.util.datatype.ColoredChar;

import java.awt.Color;
import java.awt.event.KeyEvent;

import pazi.features.IFeature;

import rogue.creature.Player;

public class HealingPotion extends Item implements IFeature<Player> {

	private int HP;
	
	public HealingPotion() {
		this(Dice.global.nextInt(50));
	}
	
	public HealingPotion(int HP){
		super(new ColoredChar('T', Color.green), "Heiltrank");
		this.HP = HP;
	}

	@Override
	public void getPickedUp(Player player) {
		player.world().removeActor(this);
		this.attach(player);
		player.addGeneralFeature(this);
		player.appendMessage("Hallelujah! Ein Heiltrank, der mir " + HP + " HP beschert!");
	}

	@Override
	public void interact(Actor actor) {
		if(Player.class.isAssignableFrom(actor.getClass())){
			((Player)actor).addHP(HP);
			actor.appendMessage("Aaaaaah, das tut gut, ich fühle mich wie auferstanden!");
		}
//		this.detach();
		this.expire();
		actor.removeFeature(this);
	}

	@Override
	public void act(Player player) {
		if(player.world().getCurrentKey() == KeyEvent.VK_H)
			interact(player);
		player.getFeatures(IFeature.class).remove(this);
	}
}
