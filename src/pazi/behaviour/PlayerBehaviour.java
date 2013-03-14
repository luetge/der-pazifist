package pazi.behaviour;

import rogue.creature.Player;

public class PlayerBehaviour implements IBehaviour<Player> {
	@Override
	public void act(Player player) {
		player.fight();
		player.walk();
	}

	@Override
	public void exit(Player actor) {}

	@Override
	public void init(Player actor) {}
}
