package pazi.behaviour;

import jade.util.datatype.Direction;
import rogue.creature.Creature;

public class PlayerBehaviour implements IBehaviour<Creature> {
	@Override
	public void act(Creature creature) {
		if(creature.hasActed())
			return;
		Direction dir = Direction.keyToDir(creature.world().getCurrentKey());
		creature.interact(dir);
		creature.walk();
	}

	@Override
	public void exit(Creature actor) {}

	@Override
	public void init(Creature actor) {}
}
