package pazi.features;

import rogue.creature.Creature;
import rogue.creature.Player;

public class Paralyzed implements IFeature<Creature> {

	int roundsToWait;
	
	public Paralyzed(int roundsToWait, Creature creature){
		this.roundsToWait = roundsToWait;
		creature.world().setMessage(creature.getName() + " wurde paralysiert! Für " + roundsToWait + " Runden!");
		creature.appendMessage("Oh Nein! Ich kann mich nicht mehr bewegen! :O ");
		System.out.println(creature.getFeatures(VisionFeature.class));
		if (creature.getClass() == Player.class)
			if (!creature.getFeatures(VisionFeature.class).isEmpty()){
				//remove old VisionFeature before applying new one!
				creature.removeFeature(creature.getFeatures(VisionFeature.class).iterator().next());
			}
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
