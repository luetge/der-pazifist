package pazi.behaviour;


import java.util.Arrays;
import java.util.Iterator;

import pazi.core.Actor;
import pazi.creature.Monster;
import pazi.creature.Player;
import pazi.path.AStar;
import pazi.util.Dice;
import pazi.util.datatype.Coordinate;
import pazi.util.datatype.Direction;


public class Follow implements IBehaviour<Monster> {

	protected static AStar pathFinder = new AStar();
	protected Actor target;
	private int radius, minDistance;
	protected int randomFactor;
	
	
	/**
	 * 
	 * @param target
	 * @param radius
	 * @param minDistance	Monster comes only This close, not closer.
	 * @param randomlyChangeDirection
	 */
	public Follow(Actor target, int radius, int distance, double randomlyChangeDirection){
		this.target = target;
		this.radius = radius;
		this.minDistance = distance;
		this.randomFactor = (int)(100*randomlyChangeDirection);
	}
	
	public Follow(Actor target, int radius, int distance){
		this(target, radius, distance, 0);
	}
	
	public Follow(Actor target, int radius, double randomlyChangeDirection){
		this(target, radius, 0, randomlyChangeDirection);
	}

	public Follow(Actor target, int radius){
		this(target, radius, 0);
	}
	
	@Override
	public void act(Monster monster) {
		double dist = 0;
		if(monster.hasActed() || target == null)
			return;

		dist = target.pos().distance(monster.pos());
		if ((monster.world().getName().equals("mainworld") && dist > radius) || dist < minDistance)
			return;
		
		if(Dice.global.chance(randomFactor)){
			monster.move(Dice.global.choose(Arrays.asList(Direction.values())));
			monster.setHasActed(true);
			return;
		}
		Iterator<Coordinate> it = pathFinder.getPartialPath(monster.world(), monster.pos(), target.pos()).iterator();
		monster.setNextCoord(it.hasNext() ? it.next() : null);
		monster.setHasActed(true);
	}

	@Override
	public void exit(Monster actor) {}

	@Override
	public void init(Monster actor) {}

}