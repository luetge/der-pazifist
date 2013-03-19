package pazi.weapons;

import jade.ui.View;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import rogue.creature.Creature;
import rogue.creature.Player;

public class Shotgun extends RCWeaponPrototype {

	public Shotgun() {
		super(0, 0, 3, 1, "Shotgun der Erlösung", new ColoredChar('p', Color.lightGray), 50);
		description = "Eine Waffe mit kurzer Reichweite und großer Streuung.";
	}

	@Override
	public int getDamage(Creature attacker, Creature victim) {
		if(attacker == null || victim == null || attacker.pos().x() != victim.x() && attacker.pos().y() != victim.pos().y() || attacker.pos().distance(victim.pos()) > getRange())
			return 0;
		return 10;
	}
	
	@Override
	public void shoot(Creature attacker, Creature victim) {
		if(Player.class.isAssignableFrom(attacker.getClass())) {
			// Player-Verhalten
			while(View.get().nextKey())
			{
				if (View.get().getKeyEvent() == Keyboard.KEY_0)
					System.out.println("hALLO");
			}
				return;
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
	protected String getEquipText() {
		return "Die Welt gehört mir!!";
	}
	
	@Override
	protected String getPickupText() {
		return "Eine Shotgun, die hätte ich gegen die Römer damals gebraucht!";
	}
}
