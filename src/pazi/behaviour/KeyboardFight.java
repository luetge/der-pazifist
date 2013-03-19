package pazi.behaviour;

import jade.util.datatype.Direction;

import org.lwjgl.input.Keyboard;

import rogue.creature.Creature;

public class KeyboardFight implements IBehaviour<Creature> {	
	@Override
	public void act(Creature creature) {
		if (creature.hasActed())
			return;
		Direction dir = Direction.keyToDir(creature.world().getCurrentKey());
		if(dir != null)
			creature.interact(dir);
		else if(creature.world().getCurrentKey() == Keyboard.KEY_SPACE)
			creature.fight(null, false);
	}

	@Override
	public void exit(Creature actor) {}

	@Override
	public void init(Creature actor) {}

}
