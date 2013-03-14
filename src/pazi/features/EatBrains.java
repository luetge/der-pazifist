package pazi.features;

import jade.util.Dice;
import jade.util.datatype.ColoredChar;

import java.awt.Color;
import java.util.ArrayList;

import rogue.creature.Creature;
import rogue.creature.Monster;

public class EatBrains implements IBeforeAfterFeature<Monster> {
	
	private int maxDist;
	private IFeature oldBehaviour;
	private Creature deadBody;
	private static final ArrayList<Color> COLORS = new ArrayList<Color>();
	
	protected final int HP = 20;
	
	public EatBrains(){
		this(5);
	}
	
	public EatBrains(int maxDist){
		this.maxDist = maxDist;
		COLORS.add(Color.magenta);
		COLORS.add(Color.cyan);
		COLORS.add(Color.yellow);
		COLORS.add(Color.orange);
	}
	
	@Override
	public void actBefore(Monster monster) {
		if(oldBehaviour == null) {
			deadBody = getDeadBodyInRange(monster, maxDist);
			if(deadBody != null){
				oldBehaviour = monster.getWalkBehaviour();
				monster.setWalkBehaviour(new Follow(deadBody, maxDist));
			}
			return;
		}
		
		if(deadBody.expired()){
			reset(monster);
			return;
		}
		
		if(deadBody.pos().equals(monster.pos())){
			monster.appendMessage("Hahahahahaha! I am eating your brains! Yumyumyumyum!");
			eatBrain(monster);
			deadBody.expire();
			deadBody = null;
			reset(monster);
			monster.setHasActed(true);
		}
	}
	
	protected void eatBrain(Monster monster){
		// 20 Leben hinzufügen
		monster.takeDamage(-HP);
	}
	
	protected void reset(Monster monster){
		monster.setWalkBehaviour(oldBehaviour);
		oldBehaviour = null;
	}
	
	private Creature getDeadBodyInRange(Monster monster, int range){
		for(Creature creature : DeadBehaviour.deadBodies){
			if(creature != null && creature.pos().distance(monster.pos()) < range && creature.getBehaviour().getClass() == DeadBehaviour.class && !creature.expired())
				return creature;
		}
		return null;
	}

	@Override
	public void actAfter(Monster actor) {}
}
