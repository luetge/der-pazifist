package pazi.behaviour;

import rogue.creature.Creature;

public class DefaultFightBehaviour implements IBehaviour<Creature> {
	/**
	 * Singleton
	 */
	private static DefaultFightBehaviour inst;
	
	private DefaultFightBehaviour(){}
	
	public static DefaultFightBehaviour getInstance(){
		if(inst == null)
			inst = new DefaultFightBehaviour();
		return inst;
	}
	
	@Override
	public void act(Creature monster) {
		if(!monster.hasActed() && monster.world().getPlayer().pos().distance(monster.pos()) < 2)
			monster.fight(monster.world().getPlayer());
			// TODO: man sollte beliebiges Ziel übergeben können!
		monster.setHasActed(true);
	}

	@Override
	public void exit(Creature actor) {}

	@Override
	public void init(Creature actor) {}
}
