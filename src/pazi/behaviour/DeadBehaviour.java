package pazi.behaviour;


import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.AbstractCollection;

import pazi.creature.Creature;
import pazi.creature.DestructableObject;
import pazi.creature.Player;
import pazi.features.IFeature;
import pazi.ui.EndScreen;
import pazi.util.datatype.ColoredChar;


public class DeadBehaviour implements IBehaviour<Creature> {
	
	// Achtung! In deadBodies sind nur die Toten mit Hirn!
	public static AbstractCollection<Creature> deadBodies = new LinkedHashSet<Creature>();
	
	public DeadBehaviour(Creature creature,Creature source){
		creature.appendMessage(creature.getDeathMessage());
		if (creature.getClass().isAssignableFrom(DestructableObject.class))
			creature.setFace(ColoredChar.create(' '));
		else
			creature.setFace(new ColoredChar(creature.face().ch(), Color.gray));
		creature.setPassable(true);
		creature.dropInventory();
		source.killedSomeone(creature);
		if(creature.isPlayer()){
			creature.expire();
			EndScreen.SetKiller(source.getIdentifier());
		}
		for (IFeature feature : creature.getFeatures(IFeature.class))
			creature.removeFeature(feature);
		deadBodies.add(creature);
	}
	
	@Override
	public void act(Creature actor) {}

	@Override
	public void exit(Creature actor) {
		if(deadBodies.contains(actor))
			deadBodies.remove(actor);
	}

	@Override
	public void init(Creature actor) {
		//TODO nötig? wird das nicht schon im Constructor gemacht?
//		deadBodies.add(actor);
	}

}
