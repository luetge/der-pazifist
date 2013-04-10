package pazi.behaviour;


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import pazi.core.Actor;
import pazi.creature.Monster;
import pazi.path.AStar;
import pazi.util.Dice;
import pazi.util.datatype.Coordinate;
import pazi.util.datatype.Direction;


public class Flee implements IBehaviour<Monster> {

	protected static AStar pathFinder = new AStar();
	protected Actor target;
	private int radius;
	protected int randomFactor;
	
	public Flee(Actor target, int radius){
		this(target, radius, 0);
	}
	
	public Flee(Actor target, int radius, double randomlyChangeDirection){
		this.target = target;
		this.radius = radius;
		this.randomFactor = (int)(100*randomlyChangeDirection);
	}
	
	@Override
	public void act(Monster monster) {
		if(!monster.hasActed() && target.pos().distance(monster.pos()) > radius)
			return;
		List<Coordinate> path = pathFinder.getPartialPath(monster.world(), monster.pos(), target.pos());
		Iterator<Coordinate> it = pathFinder.getPartialPath(monster.world(), monster.pos(), target.pos()).iterator();
		if(Dice.global.chance(randomFactor)){
			monster.move(Dice.global.choose(Arrays.asList(Direction.values())));
			monster.setHasActed(true);
			return;
		}
		if (it.hasNext()){
			int dx,dy;
			Coordinate c = it.next();
			dx = monster.pos().x()-c.x();
			dy = monster.pos().y()-c.y();
			monster.setNextCoord(monster.pos().getTranslated(dx, dy));
			monster.setHasActed(true);
		} else
			monster.setNextCoord(null);
	}

	@Override
	public void exit(Monster actor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(Monster actor) {
		// TODO Auto-generated method stub
		
	}


}
