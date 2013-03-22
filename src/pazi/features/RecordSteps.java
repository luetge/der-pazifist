package pazi.features;

import org.lwjgl.input.Keyboard;

import rogue.creature.Player;

public class RecordSteps implements IFeature<Player> {

	protected StringBuilder builder = new StringBuilder();
	protected boolean recording = false;
	
	@Override
	public void act(Player actor) {
		// Aufnehmen mit R
		if(actor.world().getCurrentKey() == Keyboard.KEY_R){
			recording = !recording;
			// Markieren des Starts/Endes
			builder.append("#|");
		} else if(actor.world().getCurrentKey() == Keyboard.KEY_T) 
			System.out.println("Aufgezeichneter Weg: " + builder.toString());
		if(!recording)
			return;
		builder.append(actor.pos() + "->");
	}
}
