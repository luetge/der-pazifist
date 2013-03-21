package pazi.behaviour;

import rogue.creature.Creature;

public class AllyWalk implements IBehaviour<Creature> {

	
	public AllyWalk(){}
	
	public void act(Creature creature) {	
			creature.walk();
	}
	@Override
	public void exit(Creature actor) {}

	@Override
	public void init(Creature actor) {}

}