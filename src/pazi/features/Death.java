package pazi.features;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;

import java.awt.Color;

import rogue.creature.Player;

public class Death implements IFeature<Actor> {

	public Death(Actor actor){
		actor.appendMessage("UUuuuuuuaaaaarrrrrrrghghhgghhh!");
		actor.setFace(new ColoredChar(actor.face().ch(), Color.gray));
		actor.setBehaviour(new DoNothing());
		actor.setPassable(true);
		if(Player.class.isAssignableFrom(actor.getClass()))
			actor.expire();
	}
	
	@Override
	public void act(Actor actor) {
	}
	
}
