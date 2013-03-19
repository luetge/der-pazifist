package pazi.features;

import rogue.creature.Monster;

public class Wuff implements IFeature<Monster> {

	protected String text = "WUFF";
	protected int radius;
	
	public Wuff(){}
		
	public Wuff(String text){
		this(text, 2);
	}
	
	public Wuff(String text, int radius){
		this.text = text;
		this.radius = radius;
	}
	
	@Override
	public void act(Monster monster) {
		double dist = monster.world().getPlayer().pos().distance(monster.pos());
		if(dist < radius && Math.random() < 0.20)
			monster.appendMessage(text);
	}
	
}
