package pazi.behaviour;

import jade.util.Guard;
import jade.util.datatype.ColoredChar;
import rogue.creature.Monster;
import rogue.creature.Player;

public class SneakStealFlee implements IBehaviour<Monster> {
	
	protected static final int MONEY_AMOUNT = 20;
	
	Player player;
	ColoredChar symbols[];
	
	public SneakStealFlee(Player player, ColoredChar symbols[]) {
		this.player = player;
		Guard.validateArgument(symbols.length==9);
		this.symbols = symbols;
	}
	
	@Override
	public void act(Monster monster) {
		if (monster.pos().distance(player.pos()) < 2){
			if(player.getInventory().getGold() > 0){
				monster.appendMessage("OMZFG!! I stealz ur monneyz lols!");
				int amount = player.getGold(-MONEY_AMOUNT);
				monster.getInventory().findGold(amount);
			} else {
				monster.appendMessage("Geh mal arbeiten, du lazy Fuck, hast ja gar keine Knete! Hier haste ne Mark, geh dich rasieren!");
				player.getGold(1);
				monster.getGold(-1);
			}
			monster.setFaces(symbols);
			monster.setFace(symbols[4]);
			
			monster.setHasActed(true);
			monster.setBehaviour(new RandomBehaviour());
		} else {
			monster.walk();
		}
	}

	@Override
	public void exit(Monster monster) {
		monster.setWalkBehaviour(new Flee(player, 5, 0.2));		
	}

	@Override
	public void init(Monster monster) {
		monster.setWalkBehaviour(new Follow(player, 5));	
	}
	
}
