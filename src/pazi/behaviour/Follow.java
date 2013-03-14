package pazi.behaviour;

import jade.core.Actor;
import jade.path.AStar;
import jade.util.Dice;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

import java.util.Arrays;
import java.util.Iterator;

import rogue.creature.Monster;
import rogue.creature.Player;

public class Follow implements IBehaviour<Monster> {

	protected static AStar pathFinder = new AStar();
	protected Actor target;
	private int radius;
	protected int randomFactor;
	
	public Follow(Actor target, int radius, double randomlyChangeDirection){
		this.target = target;
		this.radius = radius;
		this.randomFactor = (int)(100*randomlyChangeDirection);
	}
	
	public Follow(Actor target, int radius){
		this(target, radius, 0);
	}
	
	@Override
	public void act(Monster monster) {
		if(!monster.hasActed() && monster.world().getActor(Player.class).pos().distance(monster.pos()) > radius || target == null)
			return;
		if(Dice.global.chance()){
			monster.move(Dice.global.choose(Arrays.asList(Direction.values())));
			return;
		}
		Iterator<Coordinate> it = pathFinder.getPartialPath(monster.world(), monster.pos(), target.pos()).iterator();
		monster.setNextCoord(it.hasNext() ? it.next() : null);
	}

	@Override
	public void exit(Monster actor) {}

	@Override
	public void init(Monster actor) {}

}