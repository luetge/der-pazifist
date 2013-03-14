package pazi.features;

import jade.core.Actor;
import jade.path.AStar;
import jade.util.datatype.Coordinate;

import java.util.Iterator;

import rogue.creature.Monster;
import rogue.creature.Player;

public class Follow implements IFeature<Monster> {

	protected static AStar pathFinder = new AStar();
	protected Actor target;
	private int radius;
	
	public Follow(Actor target, int radius){
		this.target = target;
		this.radius = radius;
	}
	
	@Override
	public void act(Monster monster) {
		if(!monster.getHasActed() && monster.world().getActor(Player.class).pos().distance(monster.pos()) > radius)
			return;
		Iterator<Coordinate> it = pathFinder.getPartialPath(monster.world(), monster.pos(), target.pos()).iterator();
		monster.setNextCoord(it.hasNext() ? it.next() : null);
	}

}