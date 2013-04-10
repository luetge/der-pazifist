package pazi.weapons;

import java.awt.Color;

import pazi.core.Actor;
import pazi.creature.Creature;
import pazi.features.Paralyzed;
import pazi.items.Item;
import pazi.util.datatype.ColoredChar;

public class VampSword extends MeleeWeaponPrototype {

	public VampSword() {
		this(null);
	}
	
	public VampSword(Creature holder) {
		super(10, 15, 0.7, "Vampirklinge", new ColoredChar('V', Color.red), holder, 50);
		this.description = "Zieht dem Gegner 10 bis 15 HP ab und fügt sie dir hinzu.";
	}
	
	@Override
	public void shoot(Creature attacker, Creature victim){
		if(attacker != null && victim != null){
			if (ammoLeft > 0)
				reduceAmmo();
			appendMessage(attacker.world(), getWeaponFiredText(attacker, victim));
			if(Math.random() < getProb(attacker, victim)){
				int dmg = getDamage(attacker, victim);
				victim.takeDamage(dmg, attacker, true);
				attacker.addHP(dmg);
				appendMessage(victim.world(), getHitText(attacker, victim));
			}
			else
				appendMessage(attacker.world(), getMissedText(attacker, victim));
		}
	}
	
	@Override
	public String getEquipText() {
		return "Das Schwert dürstet nach dem Blut meiner Feinde. Ich spüre seine Macht.";
	}
	
	@Override
	protected String getPickupText() {
		return "Oha. Das mächtige Vampirschwert, aus dem Knochen des großen Nosferatu geschnitzt. Nice.";
	}
	
	@Override
	protected String getMissedText(Creature attacker, Creature victim) {
		return (victim.getName() + " verfehlt! Dieses Schwert ist schwer handzuhaben..");
	}
}
