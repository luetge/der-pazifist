package pazi.features;

import rogue.creature.Creature;
import rogue.creature.Creature.AttackableCreature;
import jade.core.Actor;

public class RoundhousePunch implements IFeature {

	@Override
	public void act(Actor actor) {}
	
	public void punch(Creature creature){
		for (Creature victim : creature.getCreaturesCloseby()){
			if(victim != null){
				victim.takeDamage(100);
			}
		}
			
	}

}
