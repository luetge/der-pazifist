package pazi.features;

import rogue.creature.Player;

public class PlayerBehaviour implements IFeature<Player> {
	@Override
	public void act(Player player) {
		player.fight();
		player.walk();
	}
}
