package pazi.items;

import java.awt.Color;

import pazi.core.Actor;
import pazi.creature.Creature;
import pazi.creature.Player;
import pazi.util.Dice;
import pazi.util.datatype.ColoredChar;


public class Adrenaline extends Item {

	private int rage;
	
	protected Adrenaline() {
		this(Dice.global.nextInt(20,70));
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
