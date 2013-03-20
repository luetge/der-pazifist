package pazi.behaviour;

import jade.core.Dialog;

import org.lwjgl.input.Keyboard;

import pazi.features.IFeature;
import rogue.creature.Player;

public class KeyboardGeneral implements IFeature<Player> {
	
	@Override
	public void act(Player player) {
		if(player.hasActed())
			return;
		switch(player.world().getCurrentKey()) {
			case Keyboard.KEY_H:
				player.drinkHealingPotion();
				break;
			case Keyboard.KEY_F:
				player.increaseFOV();
				break;
			case Keyboard.KEY_R:
				player.roundhousePunch();
				break;
			case Keyboard.KEY_M:
				player.meditate();
				break;
			case Keyboard.KEY_3:
				player.redeem();
				break;
			case Keyboard.KEY_F1:
				player.showHelp();
				break;
			default:
				return;
		}
	}

}
