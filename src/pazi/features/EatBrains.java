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
		if(deadBody.pos() == monster.pos()){
			monster.appendMessage("Hahahahahaha! I am eating your brains! Yumyumyumyum!");
			monster.setFace(new ColoredChar(monster.face().ch(), Dice.global.choose(COLORS)));
			deadBody.expire();
			deadBody = null;
			monster.setBehaviour(oldBehaviour);
			oldBehaviour = null;
		}
	}
	
	private Creature getDeadBodyInRange(Monster monster, int range){
		for(Creature creature : monster.world().getActors(Creature.class))
			if(creature.pos().distance(monster.pos()) < range && creature.getFeatures(Death.class).size() != 0)
				return creature;
		return null;
	}

	@Override
	public void actAfter(Monster actor) {}
}
