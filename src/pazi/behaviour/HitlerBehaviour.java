package pazi.behaviour;

import jade.core.World;
import jade.util.Dice;

import java.util.LinkedList;
import java.util.List;

import pazi.trigger.CreatureTrigger;
import pazi.trigger.TriggerFactory;
import rogue.creature.Creature;

public class HitlerBehaviour extends DefaultBehaviour {
	protected int roundsToNextBehaviour;
	protected int minRounds = 20;
	protected int maxRounds = 35;
	protected final int spawnNazisCooldown = 50;
	protected int spawnNazisCounter = 0;
	protected List<IBehaviour> walkBehaviours;
	protected World world;
	CreatureTrigger ct;
	
	public HitlerBehaviour(World world) {
		setRoundsToNextBehaviour();
		this.world = world;
	}
	
	protected void setRoundsToNextBehaviour() {
		roundsToNextBehaviour = (int)(Math.random() * (maxRounds - minRounds)) + minRounds; 
	}
	
	@Override
	public void act(Creature creature) {
		if(walkBehaviours == null)
			init(creature);
		if(++spawnNazisCounter == spawnNazisCooldown - 1){
			spawnNazisCounter = 0;
			ct.setActivated(true);
		}
		if(--roundsToNextBehaviour <= 0) {
			setRoundsToNextBehaviour();
			creature.setWalkBehaviour(Dice.global.choose(walkBehaviours));
		}
		super.act(creature);
	}
	
	@Override
	public void init(Creature actor) {
		super.init(actor);
		walkBehaviours = new LinkedList<IBehaviour>();
		walkBehaviours.add(new Follow(world.getPlayer(), 20, 0.4));
		walkBehaviours.add(new Flee(world.getPlayer(), 20, 0.6));
		ct = (CreatureTrigger)TriggerFactory.createTrigger("zombieguard", world);
		ct.attach(actor);
		world.getTrigger().add(ct);
	}
}
