package pazi.features;

import jade.core.Actor;
import rogue.creature.Player;

public class Braaaiiiiins implements IFeature {

	@Override
	public boolean act(Actor actor) {
		double dist = actor.world().getActor(Player.class).pos().distance(actor.pos());
		if(dist < 2)
			actor.appendMessage("Braaaaaaiiiiiiiiiiiiiiiiiinns!!!");
		return true;
	}
	
}
