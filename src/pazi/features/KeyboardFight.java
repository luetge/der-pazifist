package pazi.features;

import jade.util.datatype.Direction;
import rogue.creature.Creature;

public class KeyboardFight implements IFeature<Creature> {
	@Override
	public void act(Creature creature) {
		Direction dir = Direction.keyToDir(creature.world().getCurrentKey());
		if(dir != null)
			creature.fight(dir);
	}

}
