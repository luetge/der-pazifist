package pazi.weapons;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;

import java.awt.Color;

import com.sun.corba.se.impl.encoding.CodeSetConversion.BTCConverter;

import rogue.creature.Creature;
import rogue.creature.Player;

public class Shotgun extends RCWeaponPrototype {

	public Shotgun() {
		super(new ColoredChar('p', Color.lightGray), "Shotgun der Erlösung");
		description = "Eine Waffe mit kurzer Reichweite und großer Streuung.";
	}

	@Override
	public int getDamage(Creature attacker, Creature victim) {
		if(attacker.pos().x() != victim.x() && attacker.pos().y() != victim.pos().y() || attacker.pos().distance(victim.pos()) > getRange())
			return 0;
		return 10;
	}

	@Override
	public double getProb(Creature attacker, Creature victim) {
		return 1;
	}

	@Override
	public void shoot(Creature attacker, Creature victim) {
		if(Player.class.isAssignableFrom(attacker.getClass())) {
			// Player-Verhalten
		}
	}
	
	protected Coordinate[] getHitCoordinates(Creature attacker, Creature victim) {
		boolean bHorizontal = attacker.pos().y() == victim.pos().y();
		return getHitCoordinates(attacker.pos(), bHorizontal, bHorizontal ? attacker.pos().x() < victim.pos().x() : attacker.pos().y() < victim.pos().y());
	}
	
	protected Coordinate[] getHitCoordinates(Coordinate pos, boolean bHorizontal, boolean toTopRight) {
		Coordinate[] coord = new Coordinate[9];
		coord[0] = new Coordinate(1, 0);
		
		coord[1] = new Coordinate(2, 1);
		coord[2] = new Coordinate(2, 0);
		coord[3] = new Coordinate(2, -1);
		
		coord[4] = new Coordinate(3, 2);
		coord[5] = new Coordinate(3, 1);
		coord[6] = new Coordinate(3, 0);
		coord[7] = new Coordinate(3, -1);
		coord[8] = new Coordinate(3, -2);
		
		for(int i=0; i < 8; i++) {
			coord[i] = (toTopRight ? coord[i] : coord[i].getTranslated(-2*coord[i].x(), 0));
			coord[i] = (bHorizontal ? coord[i] : coord[i].getSwapped()).getTranslated(pos); 
		}
		
		return coord;
	}
	
	@Override
	public double getRange() {
		return 3;
	}

	@Override
	public void interact(Actor actor) {
		if(Creature.class.isAssignableFrom(actor.getClass())){
			((Creature)actor).setRCWeapon(this);
			actor.appendMessage("Die Welt gehört mir!!");
			setHasActed(false);
		}
	}
	
	@Override
	public void getPickedUp(Creature creature) {
		super.getPickedUp(creature);
		creature.appendMessage("Eine Shotgun, die hätte ich gegen die Römer damals gebraucht!");
	}

}
