package pazi.behaviour;


import org.lwjgl.input.Keyboard;

import pazi.core.Dialog;
import pazi.creature.Player;
import pazi.features.IFeature;

public class KeyboardGeneral implements IFeature<Player> {
	
	@Override
	public void act(Player player) {
		if(player.hasActed())
			return;
		switch(player.world().getCurrentKey()) {
		    case Keyboard.KEY_G:
		    	player.setGodMode(!player.getGodMode());
		    	break;
			case Keyboard.KEY_H:
				player.drinkHealingPotion();
				break;
			case Keyboard.KEY_1:
				player.increaseFOV();
				break;
			case Keyboard.KEY_4:
				player.roundhousePunch();
				break;
			case Keyboard.KEY_2:
				player.meditate();
				break;
			case Keyboard.KEY_3:
				player.redeem();
				break;
			case Keyboard.KEY_F1:
				player.showHelp();
				break;
			case Keyboard.KEY_Q:
				player.changeMelee();
				break;
			case Keyboard.KEY_W:
				player.changeRC();
				break;
			default:
				return;
		}
	}

}
