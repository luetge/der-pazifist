package pazi.behaviour;

import rogue.creature.Creature;
import rogue.creature.Player;

public class ParalyzerBehaviour implements IBehaviour<Creature> {
	
	Player player;
	int radius;
	
	public ParalyzerBehaviour(Player player, int radius){
		this.player = player;
		this.radius = radius;
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
		creature.setWalkBehaviour(new Follow(player, radius, 0.5));
		//TODO creature.setCloseCombatBehaviour(new ParalyzeFightBehaviour(player, 10));
	}

}
