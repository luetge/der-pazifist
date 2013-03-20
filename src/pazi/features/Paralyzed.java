package pazi.features;

import rogue.creature.Creature;
import rogue.creature.Player;

public class Paralyzed implements IFeature<Creature> {

	int roundsToWait;
	
	public Paralyzed(int roundsToWait, Creature creature){
		this(roundsToWait, null, creature);
	}
	
	public Paralyzed(int roundsToWait, Creature source, Creature victim){
		System.out.println(source.getIdentifier());
		if (source != null && source.getIdentifier() == "alien1")
			source.world().appendMessage(source.getName() + " bewirft " + victim.getName() + " mit Schleim.");
		this.roundsToWait = roundsToWait;
		victim.world().setMessage(victim.getName() + " wurde paralysiert! Für " + roundsToWait + " Runden!");
		victim.appendMessage("Oh Nein! Ich kann mich nicht mehr bewegen! :O ");
		System.out.println(victim.getFeatures(VisionFeature.class));
		if (victim.getClass() == Player.class)
			if (!victim.getFeatures(VisionFeature.class).isEmpty()){
				//remove old VisionFeature before applying new one!
				victim.removeFeature(victim.getFeatures(VisionFeature.class).iterator().next());
			}
				victim.addGeneralFeature(new VisionFeature((Player)victim, roundsToWait, 2));
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
