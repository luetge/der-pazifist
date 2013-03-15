package pazi.behaviour;

import rogue.creature.Monster;

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
		if(Math.random() < randFactor){
			monster.fight();
			monster.walk();
		}
		else {
			monster.walk();
			monster.fight();
		}
	}

	@Override
	public void exit(Monster actor) {}

	@Override
	public void init(Monster actor) {}

}
