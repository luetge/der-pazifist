package pazi.behaviour;

import java.awt.event.KeyEvent;

import pazi.features.IFeature;
import rogue.creature.Player;

public class KeyboardGeneral implements IFeature<Player> {
	
	@Override
	public void act(Player player) {
		if(player.hasActed())
			return;
		switch(player.world().getCurrentKey()) {
			case KeyEvent.VK_H:
				player.drinkHealingPotion();
				return;
			default:
				return;
		}
	}

}
