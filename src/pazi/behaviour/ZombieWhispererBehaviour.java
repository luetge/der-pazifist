package pazi.behaviour;

import jade.util.datatype.Coordinate;
import rogue.creature.CreatureFactory;
import rogue.creature.Monster;
import rogue.creature.Player;

public class ZombieWhispererBehaviour implements IBehaviour<Monster> {

	Player player;
	
	public ZombieWhispererBehaviour(Player player) {
		this.player = player;
	}
	
	@Override
	public void act(Monster monster) {
		if (monster.pos().distance(monster.world().getPlayer().pos()) <= 5){
			monster.appendMessage("Kommt, meine Freunde. Essen ist angerichtet!");
			monster.world().setMessage("Zombies graben sich rund um den Zombieflüsterer aus dem Boden!");
			callForBackup(monster);
			exit(monster);
		}
	}
	
	private void callForBackup(Monster monster) {
		Monster[] zombies;		
		zombies = new Monster[8];
		for (int i = 0; i < 8; i++){
			zombies[i] = (Monster) CreatureFactory.createCreature("zombie1", monster.world());
			Coordinate pos = monster.pos();
			switch (i) {
	        	case 0:
	        	case 1:
	        	case 2:
	        		if (monster.world().passableAt(pos.x() + i - 1, pos.y()-1))
	        			monster.world().addActor(zombies[i], pos.x() + i - 1, pos.y()-1);
	        		break;
	        	case 3:
	        		if (monster.world().passableAt(pos.x() - 1, pos.y()))
	        			monster.world().addActor(zombies[i], pos.x() - 1, pos.y());
	        		break;
	        	case 4:
	        		if (monster.world().passableAt(pos.x() + 1, pos.y()))
	        			monster.world().addActor(zombies[i], pos.x() + 1, pos.y());
	        		break;
	        	case 5:	        	
	        	case 6:
	        	case 7:
	        		if (monster.world().passableAt(pos.x() + i - 6, pos.y() + 1))
	        			monster.world().addActor(zombies[i], pos.x() + i - 6, pos.y() + 1);
	        		break;
	        	default:
	        			break;
			}
		}		
	}

	@Override
	public void exit(Monster monster) {
			monster.appendMessage("Meine Arbeit hier ist getan. Freitod ftw!");
			monster.expire();
	}

	@Override
	public void init(Monster monster) {
		monster.setWalkBehaviour(new Follow(player, 20, 4, 0.2));		
	}

}
