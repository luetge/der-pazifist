package pazi.features;

import jade.core.Actor;
import rogue.creature.Creature;


public class Walking implements IFeature {

	@Override
	public boolean act(Actor actor) {
		if(Creature.class.isAssignableFrom(actor.getClass())){
			((Creature)actor).walk();
			return true;
		}
		return false;
	}

}
