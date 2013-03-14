package pazi.behaviour;

import jade.core.Actor;
import jade.path.AStar;
import jade.util.datatype.Coordinate;

import java.util.Iterator;


import rogue.creature.Monster;
import rogue.creature.Player;

public class Follow implements IBehaviour<Monster> {

	protected static AStar pathFinder = new AStar();
	protected Actor target;
	private int radius;
	
	public Follow(Actor target, int radius){
		this.target = target;
		this.radius = radius;
	}
	
	@Override
	public void act(Monster monster) {
		if(!monster.hasActed() && monster.world().getActor(Player.class).pos().distance(monster.pos()) > radius || target == null)
			return;
		Iterator<Coordinate> it = pathFinder.getPartialPath(monster.world(), monster.pos(), target.pos()).iterator();
		monster.setNextCoord(it.hasNext() ? it.next() : null);
	}

	@Override
	public void exit(Monster actor) {}

	@Override
	public void init(Monster actor) {}

}