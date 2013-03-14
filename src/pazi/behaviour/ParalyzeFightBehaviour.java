package pazi.behaviour;

import pazi.features.Paralyzed;
import rogue.creature.Creature;

public class ParalyzeFightBehaviour implements IBehaviour<Creature> {
	
	int cooldown;
	int counter;
	Creature target;
	
	public ParalyzeFightBehaviour(Creature target, int cooldown){
		this.cooldown = cooldown;
		this.target = target;
		counter = cooldown;
	}
	
	public void act(Creature monster) {
		if (counter < cooldown){
			counter++;
			return;
		}
		
		if(!monster.hasActed() && monster.world().getPlayer().pos().distance(monster.pos()) < 2){
			target.addGeneralFeature(new Paralyzed(2));
			monster.setHasActed(true);
			counter = 0;
			monster.appendMessage("Freeze, Motherfucker!");
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
