package pazi.behaviour;

import rogue.creature.Creature;

public class DefaultFightBehaviour implements IBehaviour<Creature> {
	@Override
	public void act(Creature monster) {
		if(!monster.hasActed() && monster.world().getPlayer().pos().distance(monster.pos()) < 2)
			monster.fight(monster.world().getPlayer());
		monster.setHasActed(true);
	}

	@Override
	public void exit(Creature actor) {}

	@Override
	public void init(Creature actor) {}
}
