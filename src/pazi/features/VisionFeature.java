package pazi.features;

import rogue.creature.Player;

public class VisionFeature implements IFeature<Player> {

	int roundsLeft;
	int oldViewFieldRadius;
	boolean seeAll;
	
	/**
	 * Keine Sichtfeldeinschränkung mehr für rounds Runden.
	 * @param player
	 * @param rounds
	 */
	public VisionFeature(Player player, int rounds) {
		this.roundsLeft = rounds;
		player.world().useViewfield(false);
		seeAll = true;
	}
	
	/**
	 * Custom radius for fov.
	 * @param player
	 * @param rounds
	 * @param radius: new Radius for fov.
	 */
	public VisionFeature(Player player, int rounds, int radius){
		this.roundsLeft = rounds;
		oldViewFieldRadius = player.getViewFieldRadius();
		player.setViewFieldRadius(radius);
		seeAll = false;
	}
	
	
	@Override
	public void act(Player player) {
		if (roundsLeft > 0)
			roundsLeft--;
		else {
			if (seeAll) 
				player.world().useViewfield(true);
			else
				player.setViewFieldRadius(oldViewFieldRadius);
			player.removeFeature(this);
		}
		
	}

}
