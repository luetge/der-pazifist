package pazi.behaviour;

import rogue.creature.Monster;

public class RandomBehaviour implements IBehaviour<Monster> {

	private double randFactor;
	
	public RandomBehaviour(){
		this(0.5);
	}
	
	public RandomBehaviour(double randFactor){
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
