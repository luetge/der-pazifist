package pazi.behaviour;

import rogue.creature.Creature;
import rogue.creature.Creature.AttackableCreature;
import rogue.creature.Player;

public class DefaultBehaviour implements IBehaviour<Creature> {

	private double offset;
	
	public DefaultBehaviour(){
		this(0.5);
	}
	/**
	 * 
	 * @param offset: The monster fights, if the probability of the next hit is at least offset
	 */
	public DefaultBehaviour(double offset){
		this.offset = offset;
	}
	
	@Override
	public void act(Creature creature) {
		AttackableCreature creat = creature.getAttackableCreature(Player.class);
		if(creat != null && (creat.melee || creat.prob >= offset)){
			creature.fight(creat.creature, creat.melee);
			creature.walk();
		}
		else {
			creature.walk();
			if(creat != null)
				creature.fight(creat.creature, creat.melee);
		}
	}

	@Override
	public void exit(Creature actor) {}

	@Override
	public void init(Creature actor) {}

}