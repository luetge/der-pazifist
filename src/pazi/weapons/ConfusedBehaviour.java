package pazi.weapons;

import jade.core.Actor;
import jade.util.Guard;
import jade.util.datatype.Direction;
import pazi.behaviour.IBehaviour;

public class ConfusedBehaviour implements IBehaviour {

	protected IBehaviour oldBehaviour;
	protected int currentKey;
	protected int rounds;
	protected double prob;
	
	public ConfusedBehaviour(IBehaviour oldBehaviour, int rounds, double prob) {
		Guard.argumentIsNotNull(oldBehaviour);
		this.oldBehaviour = oldBehaviour;
		this.rounds = rounds;
		this.prob = prob;
	}
	
	@Override
	public void act(Actor actor) {
		currentKey = actor.world().getCurrentKey();
		if(Math.random() <= prob && Direction.keyToDir(currentKey) != null)
			actor.world().setCurrentKey(Direction.getRandomDirKey());
		oldBehaviour.act(actor);
		actor.world().setCurrentKey(currentKey);
		if(--rounds == 0)
			actor.setBehaviour(oldBehaviour);
	}

	@Override
	public void init(Actor actor) { }

	@Override
	public void exit(Actor actor) {
		actor.appendMessage("Endlich hat sich die Verwirrung gelegt, puh!");
	}

}
