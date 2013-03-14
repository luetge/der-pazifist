package pazi.features;

import rogue.creature.Monster;

public class Braaaiiiiins implements IFeature<Monster> {

	/**
	 * Singleton
	 */
	private static Braaaiiiiins inst;
	
	private Braaaiiiiins(){}
	
	public static Braaaiiiiins getInstance(){
		if(inst == null)
			inst = new Braaaiiiiins();
		return inst;
	}
	
	@Override
	public void act(Monster monster) {
		double dist = monster.world().getPlayer().pos().distance(monster.pos());
		if(dist < 2 && Math.random() < 0.05)
			monster.appendMessage("Braaaaaaiiiiiiiiiiiiiiiiiinns!!!");
	}
	
}
