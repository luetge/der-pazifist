package pazi.features;

import rogue.creature.Monster;
/**
 * 
 *	First Fight, Then Walk! With Prob. Given To Constructor, else 0.5
 *	Otherwise: First Walk, Then Fight!
 *
 */
public class RandomBehaviour implements IFeature<Monster> {

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

}
