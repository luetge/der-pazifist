package pazi.features;

import rogue.creature.Monster;

public class Braaaiiiiins implements IFeature<Monster> {

	protected String text = "Braaaaaaiiiiiiiiiiiiiiiiiinns!!!";
	protected int radius;
	
	public Braaaiiiiins(){}
		
	public Braaaiiiiins(String text){
		this(text, 2);
	}
	
	public Braaaiiiiins(String text, int radius){
		this.text = text;
		this.radius = radius;
	}
	
	@Override
	public void act(Monster monster) {
		double dist = monster.world().getPlayer().pos().distance(monster.pos());
		if(dist < radius && Math.random() < 0.05)
			monster.appendMessage(text);
	}
	
}
