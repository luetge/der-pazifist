package pazi.features;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;

import java.awt.Color;

import rogue.creature.Creature;

public class Death implements IFeature<Actor> {

	public Death(Actor actor){
		actor.appendMessage("UUuuuuuuaaaaarrrrrrrghghhgghhh!");
		actor.setFace(new ColoredChar(actor.face().ch(), Color.gray));
		if(Creature.class.isAssignableFrom(actor.getClass()))
				((Creature)actor).neutralize();
	}
	
	@Override
	public boolean act(Actor actor) {
		return false;
	}
	
}
