package pazi.behaviour;

import rogue.creature.Creature;
import rogue.creature.Monster;
import rogue.creature.Player;

public class DefaultRandomBehaviour implements IBehaviour<Monster> {

	private double randFactor;
	
	public DefaultRandomBehaviour(){
		this(0.5);
	}
	/**
	 * 
	 * @param randFactor: With This Prob. The Monster Fights Before It Walks.
	 */
	public DefaultRandomBehaviour(double randFactor){
		this.randFactor = randFactor;
	}
	
	@Override
	public void act(Monster monster) {
		Creature creat = monster.getAttackableCreature(Player.class);
		if(creat != null && Math.random() < randFactor){
			monster.fight(creat, true);
			monster.walk();
		}
		else {
			monster.walk();
			monster.fight(creat, true);
		}
	}

	@Override
	public void exit(Monster actor) {}

	@Override
	public void init(Monster actor) {}

}
