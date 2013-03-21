package pazi.items;

import java.awt.Color;

import rogue.creature.Creature;
import rogue.creature.Player;
import jade.core.Actor;
import jade.util.Dice;
import jade.util.datatype.ColoredChar;

public class Adrenaline extends Item {

	private int rage;
	
	protected Adrenaline() {
		this(Dice.global.nextInt(49)+1);
	}
	
	protected Adrenaline(int rage){
		super(new ColoredChar('A', Color.green), "Adrenalinspritze (" + rage + "% Rage)");
		this.rage = rage;
		description = "Eine Adrenalinspritze, die die Rage um " + rage + " Prozent erhöht.";
	}

	@Override
	public void getPickedUp(Creature creature) {
		super.getPickedUp(creature);
		creature.appendMessage("Allein der Anblick treibt mich zur Weißglut!");
	}

	@Override
	public void interact(Actor actor) {
		if(actor.isPlayer()){
			((Player)actor).addRage(rage);
			actor.appendMessage("Wuaaaaah, ich muss töten!!");
			((Player)actor).setHasActed(true);
		}
	}

}
