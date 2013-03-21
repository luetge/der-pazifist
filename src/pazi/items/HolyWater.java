package pazi.items;

import java.awt.Color;

import rogue.creature.Creature;
import rogue.creature.Player;
import jade.core.Actor;
import jade.util.Dice;
import jade.util.datatype.ColoredChar;

public class HolyWater extends Item {

	private int faith;
	
	protected HolyWater() {
		this(Dice.global.nextInt(49)+1);
	}
	
	protected HolyWater(int faith){
		super(new ColoredChar('A', Color.green), "Weihwasser (" + faith + "% Glauben)");
		this.faith = faith;
		description = "Etwas Weihwasser, welches den Glauben um " + faith + " Prozent erhöht.";
	}

	@Override
	public void getPickedUp(Creature creature) {
		super.getPickedUp(creature);
		creature.appendMessage("Ein wenig meines Vaters Wasser, das sollte meinem Glauben gut tun.");
	}

	@Override
	public void interact(Actor actor) {
		if(actor.isPlayer()){
			((Player)actor).addFaith(faith);
			actor.appendMessage("Besser als Meditieren, das Wässerchen!");
			((Player)actor).setHasActed(true);
		}
	}

}
