package rogue.behaviour;

import java.util.Arrays;

import jade.core.Actor;
import jade.util.Dice;
import jade.util.datatype.Direction;
import pazi.behaviour.IBehaviour;

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
