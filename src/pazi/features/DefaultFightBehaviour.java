package pazi.features;

import rogue.creature.Creature;

public class DefaultFightBehaviour implements IFeature<Creature> {
	@Override
	public void act(Creature monster) {
		if(monster.world().getPlayer().pos().distance(monster.pos()) < 2)
			monster.fight(monster.world().getPlayer());
	}
}
