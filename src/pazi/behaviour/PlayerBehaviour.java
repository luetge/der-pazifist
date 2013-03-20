package pazi.behaviour;

import org.lwjgl.input.Keyboard;

import jade.util.datatype.Direction;
import rogue.creature.Creature;

public class PlayerBehaviour implements IBehaviour<Creature> {
	@Override
	public void act(Creature creature) {
		if(creature.hasActed())
			return;
		if(creature.world().getCurrentKey() == Keyboard.KEY_SPACE)
			creature.fight(null, false);
		Direction dir = Direction.keyToDir(creature.world().getCurrentKey());
		creature.interact(dir);
		creature.walk();
	}

	@Override
	public void exit(Creature actor) {}

	@Override
	public void init(Creature actor) {}
}
