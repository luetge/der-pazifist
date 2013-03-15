package pazi.behaviour;

import pazi.features.Paralyzed;
import rogue.creature.Creature;

public class DefaultRangedCombatBehaviour implements IBehaviour<Creature> {

	private int range;
	private double hitProbability;
	private int maxDamage, minDamage;
	
	
	public DefaultRangedCombatBehaviour(int range, double hitProbability, int maxDamage, int minDamage) {
		this.range = range;
		this.hitProbability = hitProbability;
		this.maxDamage = maxDamage;
		this.minDamage = minDamage;
	}
	
	
	@Override
	public void act(Creature monster) {
		if(!monster.hasActed() && monster.world().getPlayer().pos().distance(monster.pos()) <= range){
			shoot(monster);
			// TODO: man sollte beliebiges Ziel übergeben können!
			monster.setHasActed(true);		// Ein Schuss ist ein Zug. Daneben zählt auch.
			monster.addGeneralFeature(new Paralyzed(2));	// cooldown von 2 Runden
			
		}
	}
	
	private void shoot(Creature monster){
		monster.appendMessage(monster.getName() + " legt an und ...");
		if (Math.random() < hitProbability){
			monster.fight(monster.world().getPlayer(), maxDamage, minDamage);
			monster.appendMessage("... trifft! Autsch.");
		} else {
			monster.appendMessage("... schießt meilenweit daneben! Du lachst ihn aus.");
		}

	}

	@Override
	public void exit(Creature actor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(Creature actor) {
		// TODO Auto-generated method stub
		
	}


}
