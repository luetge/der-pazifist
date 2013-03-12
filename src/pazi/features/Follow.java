package pazi.features;

import jade.path.AStar;
import rogue.creature.Monster;

public class Follow implements IFeature<Monster> {

	protected static AStar pathFinder = new AStar();
	
	@Override
	public boolean act(Monster monster) {
		monster.setNextCoord(pathFinder.getPartialPath(monster.world(), monster.pos(), monster.world().getPlayer().pos()).iterator().next());
		return true;
	}

}
