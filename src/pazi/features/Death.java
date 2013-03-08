package pazi.features;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;

import java.awt.Color;

public class Death implements IFeature {

	public Death(Actor actor){
		System.out.println("Ich bin tot!");
		actor.setFace(new ColoredChar(actor.face().ch(), Color.gray));
	}
	
	@Override
	public boolean act(Actor actor) {
		return false;
	}
	
}
