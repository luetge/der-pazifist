package pazi.behaviour;

import jade.util.datatype.Direction;
import rogue.creature.Creature;

public class KeyboardWalk implements IBehaviour<Creature> {
	@Override
	public void act(Creature creature) {
		Direction dir = Direction.keyToDir(creature.world().getCurrentKey());
		if(dir != null)
			creature.move(dir);
	}

	@Override
	public void exit(Creature actor) {}

	@Override
	public void init(Creature actor) {}
}