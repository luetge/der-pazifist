package pazi.features;

import jade.util.datatype.ColoredChar;

import java.awt.Color;
import java.util.ArrayList;

import rogue.creature.Creature;
import rogue.creature.Player;

public class DeadBehaviour implements IFeature<Creature> {
	
	public static ArrayList<Creature> deadBodies = new ArrayList<Creature>();
	
	public DeadBehaviour(Creature creature){
		creature.appendMessage("UUuuuuuuaaaaarrrrrrrghghhgghhh!");
		creature.setFace(new ColoredChar(creature.face().ch(), Color.gray));
		creature.setPassable(true);
		if(Player.class.isAssignableFrom(creature.getClass()))
			creature.expire();
		deadBodies.add(creature);
	}
	
	@Override
	public void act(Creature actor) {}

}
