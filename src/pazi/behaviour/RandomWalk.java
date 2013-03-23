package pazi.behaviour;

import java.util.Arrays;

import pazi.core.Actor;
import pazi.util.Dice;
import pazi.util.datatype.Direction;

public class RandomWalk implements IBehaviour {

	@Override
	public void act(Actor actor) {
		actor.move(Dice.global.choose(Arrays.asList(Direction.values())));
		actor.setHasActed(true);
	}

	@Override
	public void exit(Actor actor) {}

	@Override
	public void init(Actor actor) {}

}
