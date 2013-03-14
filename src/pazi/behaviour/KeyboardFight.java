package pazi.behaviour;

import jade.util.datatype.Direction;
import rogue.creature.Creature;

public class KeyboardFight implements IBehaviour<Creature> {
	@Override
	public void act(Creature creature) {
		if (creature.getHasActed())
			return;
		Direction dir = Direction.keyToDir(creature.world().getCurrentKey());
		if(dir != null)
			creature.fight(dir);
	}

	@Override
	public void exit(Creature actor) {}

	@Override
	public void init(Creature actor) {}

}
