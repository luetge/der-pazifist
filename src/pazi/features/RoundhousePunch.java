package pazi.features;

import jade.core.Actor;
import rogue.creature.Creature;

public class RoundhousePunch implements IFeature {

	@Override
	public void act(Actor actor) {}
	
	public void punch(Creature creature){
		for (Creature victim : creature.getCreaturesCloseby()){
			if(victim != null){
				System.out.println(victim.toString());
				victim.takeDamage(100, creature);
				System.out.println("BÄM");
			}
		}
			
	}

}
