package pazi.behaviour;

import jade.util.datatype.ColoredChar;

import java.awt.Color;
import java.util.ArrayList;

import rogue.creature.Creature;
import rogue.creature.Player;

public class DeadBehaviour implements IBehaviour<Creature> {
	
	// Achtung! In deadBodies sind nur die Toten mit Hirn!
	public static ArrayList<Creature> deadBodies = new ArrayList<Creature>();
	
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
	
	public DeadBehaviour(Creature creature,Creature source){
		creature.appendMessage("UUuuuuuuaaaaarrrrrrrghghhgghhh!");
		source.gainXp(creature.getXp());
		creature.setFace(new ColoredChar(creature.face().ch(), Color.gray));
		creature.setPassable(true);
		creature.dropInventory();
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
