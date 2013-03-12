package pazi.features;

import rogue.creature.Monster;
import rogue.creature.Player;

public class Braaaiiiiins implements IFeature<Monster> {

	@Override
	public boolean act(Monster monster) {
		double dist = monster.world().getActor(Player.class).pos().distance(monster.pos());
		if(dist < 2 && Math.random() < 0.05)
			monster.appendMessage("Braaaaaaiiiiiiiiiiiiiiiiiinns!!!");
		return true;
	}
	
}
