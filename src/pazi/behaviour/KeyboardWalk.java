package pazi.behaviour;

import pazi.creature.Creature;
import pazi.util.datatype.Direction;

public class KeyboardWalk implements IBehaviour<Creature> {
	@Override
	public void act(Creature creature) {
		if (creature.hasActed())
			return;
		Direction dir = Direction.keyToDir(creature.world().getCurrentKey());
		if(dir != null)
			creature.move(dir);
	}

	@Override
	public void exit(Creature actor) {}

	@Override
	public void init(Creature actor) {}
}
