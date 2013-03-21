package pazi.behaviour;

import rogue.creature.Creature;

public class AllyBehaviour implements IBehaviour<Creature> {
	
	public AllyBehaviour()
	{
	}

	@Override
	public void act(Creature creature) {
		creature.walk();
	}

	@Override
	public void init(Creature creature) {
	}

	@Override
	public void exit(Creature creature) {
	}

}
