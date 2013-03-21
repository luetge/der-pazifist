package pazi.weapons;

import jade.ui.View;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

import java.awt.Color;

import rogue.creature.Ally;
import rogue.creature.Creature;

public class Shotgun extends RCWeaponPrototype {

	public Shotgun() {
		super(0, 0, 3, 1, "Shotgun der Erlösung", new ColoredChar('p',
				Color.lightGray), 50);
		description = "Eine Waffe mit kurzer Reichweite und großer Streuung.";
	}

	@Override
	public int getDamage(Creature attacker, Creature victim) {
		if (attacker == null || victim == null
				|| attacker.pos().distance(victim.pos()) > getRange())
			return 0;
		return (int) (getRange() + 1 - attacker.pos().distance(victim.pos())) * 30;
	}

	@Override
	public void shoot(Creature attacker, Creature victim) {
		if(attacker.isPlayer()) {
			// Player-Verhalten
			while (!View.get().nextKey())
				View.get().update();
			Direction dir = Direction.keyToDir(View.get().getKeyEvent());
			if (dir == null || Math.abs(dir.dx()) + Math.abs(dir.dy()) != 1)
				return;
			else
				shoot(attacker, dir);

			return;
		}
	}

	private void shoot(Creature attacker, Direction dir) {
		Coordinate[] coords = getHitCoordinates(attacker.pos(), dir);
		setHasActed(true);
		if (coords == null)
			return;
		if (hitCoord(attacker, coords[0]))
			return;
		if (!hitCoord(attacker, coords[1])) {
			hitCoord(attacker, coords[4]);
			hitCoord(attacker, coords[5]);
		}
		if (!hitCoord(attacker, coords[2]))
			hitCoord(attacker, coords[6]);
		if (!hitCoord(attacker, coords[3])) {
			hitCoord(attacker, coords[7]);
			hitCoord(attacker, coords[8]);
		}
	}

	private boolean hitCoord(Creature attacker, Coordinate pos) {
		if (pos.x() < 0 || pos.y() < 0)
			return false;
		if (pos.x() >= attacker.world().width() || pos.y() >= attacker.world().height())
			return false;
		for (Creature creature : attacker.world().getActorsAt(Creature.class, pos)) {
			if (!creature.isPassable() && !Ally.class.isAssignableFrom(creature.getClass())) {
				super.shoot(attacker, creature);
				return true;
			}
		}
		return false;
	}

	protected Coordinate[] getHitCoordinates(Creature attacker, Creature victim) {
		return getHitCoordinates(attacker.pos(),
				attacker.pos().directionTo(victim.pos()));
	}

	protected Coordinate[] getHitCoordinates(Coordinate pos, Direction dir) {

		if (dir == null || Math.abs(dir.dx()) + Math.abs(dir.dy()) != 1
				|| pos == null)
			return null;

		Direction perpendicular = getPerpendicularDirection(dir);

		Coordinate[] coord = new Coordinate[9];
		coord[0] = pos.getTranslated(dir);

		coord[1] = coord[0].getTranslated(dir);
		coord[2] = coord[1].getTranslated(perpendicular);
		coord[3] = coord[1].getTranslated(getOppositeDirection(perpendicular));

		coord[4] = coord[1].getTranslated(dir);
		coord[5] = coord[4].getTranslated(perpendicular);
		coord[6] = coord[5].getTranslated(perpendicular);
		coord[7] = coord[4].getTranslated(getOppositeDirection(perpendicular));
		coord[8] = coord[7].getTranslated(getOppositeDirection(perpendicular));

		return coord;
	}

	protected Direction getPerpendicularDirection(Direction dir) {
		switch (dir) {
		case NORTH:
			return Direction.EAST;
		case SOUTH:
			return Direction.WEST;
		case WEST:
			return Direction.NORTH;
		case EAST:
			return Direction.SOUTH;
		default:
			return null;
		}
	}

	protected Direction getOppositeDirection(Direction dir) {
		switch (dir) {
		case NORTH:
			return Direction.SOUTH;
		case SOUTH:
			return Direction.NORTH;
		case WEST:
			return Direction.EAST;
		case EAST:
			return Direction.WEST;
		default:
			return null;
		}
	}

	@Override
	protected String getEquipText() {
		return "Die Welt gehört mir!!";
	}

	@Override
	protected String getPickupText() {
		return "Eine Shotgun, die hätte ich gegen die Römer damals gebraucht!";
	}
}
