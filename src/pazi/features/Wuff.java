package pazi.features;

import rogue.creature.Monster;

public class Wuff implements IFeature<Monster> {

	protected String text = "WUFF";
	
	public Wuff(){}
		
	
	public Wuff(String text){
		this.text = text;
		
	}
	
	@Override
	public void act(Monster monster) {
		
		if(Math.random() < 0.20)
			monster.appendMessage(text);
	}
	
}
