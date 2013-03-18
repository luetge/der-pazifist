package pazi.behaviour;

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
	
	/**
	 * Singleton
	 */
	private static DeadBehaviour inst;
	
	private DeadBehaviour(){}
	
	public static DeadBehaviour getInstance(){
		if(inst == null)
			inst = new DeadBehaviour();
		return inst;
	}
	
	public DeadBehaviour(Creature creature){
		creature.appendMessage("UUuuuuuuaaaaarrrrrrrghghhgghhh!");
		creature.setFace(new ColoredChar(creature.face().ch(), Color.gray));
		creature.setPassable(true);
		creature.dropInventory();
		creature.world().getPlayer().killedSomeone(creature);
		if(Player.class.isAssignableFrom(creature.getClass()))
			creature.expire();
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
		deadBodies.add(actor);
	}

}
