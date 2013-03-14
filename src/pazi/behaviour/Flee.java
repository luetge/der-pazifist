package pazi.behaviour;

import jade.core.Actor;
import jade.path.AStar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

import java.util.Iterator;

import pazi.features.IFeature;

import rogue.creature.Monster;
import rogue.creature.Player;

public class Flee implements IBehaviour<Monster> {

	protected static AStar pathFinder = new AStar();
	protected Actor target;
	private int radius;
	
	public Flee(Actor target, int radius){
		this.target = target;
		this.radius = radius;
	}
	
	@Override
	public void act(Monster monster) {
		if(!monster.getHasActed() && target.pos().distance(monster.pos()) > radius)
			return;
		Iterator<Coordinate> it = pathFinder.getPartialPath(monster.world(), monster.pos(), target.pos()).iterator();
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
