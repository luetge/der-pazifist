package pazi.behaviour;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;
import pazi.features.IBeforeAfterFeature;
import rogue.creature.Monster;
import rogue.creature.Player;

public class SneakStealFlee implements IBehaviour<Monster> {
	
	Player player;
	Character symbol;
	
	public SneakStealFlee(Player player, Character symbol) {
		this.player = player;
		this.symbol = symbol;
	}
	
	@Override
	public void act(Monster monster) {
		if (monster.pos().distance(player.pos()) < 2){
			monster.appendMessage("OMZFG!! I stealz ur monneyz lols!");
			monster.setAllFaces(new ColoredChar(symbol, monster.face().color()));
			player.getGold(-20);
			monster.setHasActed(true);
			monster.setBehaviour(new RandomBehaviour());
		} else {
			monster.walk();
		}
	}

	@Override
	public void exit(Monster monster) {
		monster.setWalkBehaviour(new Flee(player, 5));		
	}

	@Override
	public void init(Monster monster) {
		monster.setWalkBehaviour(new Follow(player, 20));	
	}
	
}
