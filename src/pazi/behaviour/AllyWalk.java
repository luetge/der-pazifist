package pazi.behaviour;


import java.util.Arrays;
import java.util.Iterator;

import pazi.creature.Creature;
import pazi.util.Dice;
import pazi.util.datatype.Coordinate;
import pazi.util.datatype.Direction;


public class AllyWalk implements IBehaviour<Creature> {

	private int walkchance;
	
	public AllyWalk()
	{
		this(33);
	}
	
	public AllyWalk(int walkchance)
	{
		this.walkchance = walkchance;
	}
	
	public void act(Creature creature) {	
		if(creature.hasActed())
			return;

		if(Dice.global.chance(walkchance)){
			Direction dir = Dice.global.choose(Arrays.asList(Direction.values())); 
			creature.move(dir);
			creature.setCurrentFace(dir.getID());
			creature.setHasActed(true);
			return;
		}
		creature.setHasActed(true);
	}
	@Override
	public void exit(Creature actor) {}

	@Override
	public void init(Creature actor) {}

}