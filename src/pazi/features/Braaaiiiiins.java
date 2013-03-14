package pazi.features;

import rogue.creature.Monster;

public class Braaaiiiiins implements IFeature<Monster> {

	protected String text = "Braaaaaaiiiiiiiiiiiiiiiiiinns!!!";
	
	public Braaaiiiiins(){}
		
	public Braaaiiiiins(String text){
		this.text = text;
	}
	
	@Override
	public void act(Monster monster) {
		double dist = monster.world().getPlayer().pos().distance(monster.pos());
		if(dist < 2 && Math.random() < 0.05)
			monster.appendMessage(text);
	}
	
}
