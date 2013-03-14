package pazi.features;

import jade.core.Actor;
import jade.path.AStar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

import java.util.Iterator;

import rogue.creature.Monster;
import rogue.creature.Player;

public class Flee implements IFeature<Monster> {

	protected static AStar pathFinder = new AStar();
	protected Actor target;
	private int radius;
	
	public Flee(Actor target, int radius){
		this.target = target;
		this.radius = radius;
	}
	
	@Override
	public void act(Monster monster) {
		if(!monster.getHasActed() && monster.world().getActor(Player.class).pos().distance(monster.pos()) > radius)
			return;
		Iterator<Coordinate> it = pathFinder.getPartialPath(monster.world(), monster.pos(), target.pos()).iterator();
		if (it.hasNext()){
			int dx,dy;
			dx = monster.pos().x()-it.next().x();
			dy = monster.pos().y()-it.next().y();
			monster.setNextCoord(monster.pos().getTranslated(-dx, -dy));
			monster.setHasActed(true);
		}
	}


}
