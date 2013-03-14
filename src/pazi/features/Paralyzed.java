package pazi.features;

import rogue.creature.Creature;

public class Paralyzed implements IFeature<Creature> {

	int roundsToWait;
	
	public Paralyzed(int roundsToWait){
		this.roundsToWait = roundsToWait;
	}

	@Override
	public void act(Creature creature) {
		if (roundsToWait < 1)
			creature.getFeatures(IFeature.class).remove(this);
		else
			roundsToWait--;
	}

}
