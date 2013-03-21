package pazi.behaviour;

import jade.util.Dice;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

import java.util.Arrays;
import java.util.Iterator;

import rogue.creature.Creature;

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
			creature.move(Dice.global.choose(Arrays.asList(Direction.values())));
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