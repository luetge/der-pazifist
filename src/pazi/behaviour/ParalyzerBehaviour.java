package pazi.behaviour;

import rogue.creature.Creature;
import rogue.creature.Player;

public class ParalyzerBehaviour implements IBehaviour<Creature> {
	
	Player player;
	
	public ParalyzerBehaviour(Player player){
		this.player = player;
	}

	@Override
	public void act(Creature creature) {
		creature.fight();
		creature.walk();
		
	}

	@Override
	public void exit(Creature creature) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(Creature creature) {
		creature.setWalkBehaviour(new Follow(player, 5, 30));
		creature.setFightBehaviour(new ParalyzeFightBehaviour(player, 10));
	}

}
