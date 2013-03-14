package pazi.features;

import rogue.creature.Creature;
import rogue.creature.Monster;

public class RevealAndSteal implements IFeature<T> {
	
	Character symbol;

	public RevealAndSteal(Character symbol) {
		this.symbol = symbol;
	}

	@Override
	public void act(Monster monster) {
		double dist = monster.world().getPlayer().pos().distance(monster.pos());
		if(dist < 2)
		{
			monster.setFace(symbol);
			monster.
		}
		
	}
	
	

}
