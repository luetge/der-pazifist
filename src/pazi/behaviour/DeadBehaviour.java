package pazi.behaviour;

import jade.ui.EndScreen;
import jade.util.datatype.ColoredChar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.AbstractCollection;

import rogue.creature.Creature;
import rogue.creature.Player;

public class DeadBehaviour implements IBehaviour<Creature> {
	
	// Achtung! In deadBodies sind nur die Toten mit Hirn!
	public static AbstractCollection<Creature> deadBodies = new LinkedHashSet<Creature>();
	
	public DeadBehaviour(Creature creature,Creature source){
		creature.appendMessage(creature.getDeathMessage());
		source.gainXp(creature.getXp());
		creature.setFace(new ColoredChar(creature.face().ch(), Color.gray));
		creature.setPassable(true);
		creature.dropInventory();
		source.killedSomeone(creature);
		if(creature.isPlayer()){
			creature.expire();
			EndScreen.SetKiller(source.getIdentifier());
		}
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
