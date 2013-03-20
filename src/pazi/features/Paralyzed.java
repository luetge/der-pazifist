package pazi.features;

import rogue.creature.Creature;
import rogue.creature.Player;

public class Paralyzed implements IFeature<Creature> {

	int roundsToWait;
	
	public Paralyzed(int roundsToWait, Creature creature){
		this.roundsToWait = roundsToWait;
		creature.world().setMessage(creature.getName() + " wurde paralysiert! Für " + roundsToWait + " Runden!");
		creature.appendMessage("Oh Nein! Ich kann mich nicht mehr bewegen! :O ");
		if (creature.getClass() == Player.class)
			creature.addGeneralFeature(new VisionFeature((Player)creature, roundsToWait, 2));
	}

	@Override
	public void act(Creature creature) {
		if (roundsToWait < 1)
			creature.removeFeature(this);
		else {
			roundsToWait--;
			creature.setHasActed(true);
		}
	}
}
