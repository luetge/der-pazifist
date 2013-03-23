package pazi.weapons;

import pazi.behaviour.IBehaviour;
import pazi.core.Actor;
import pazi.util.Guard;
import pazi.util.datatype.Direction;

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
	
	public void setRounds(int rounds){
		this.rounds = rounds;
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
