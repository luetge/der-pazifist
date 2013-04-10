package pazi.weapons;

import java.awt.Color;

import pazi.core.Actor;
import pazi.creature.Creature;
import pazi.features.Paralyzed;
import pazi.items.Item;
import pazi.util.datatype.ColoredChar;

public class FrostSword extends MeleeWeaponPrototype {

	public FrostSword(){
		this(null);
	}
	
	public FrostSword(Creature holder) {
		super(0, 0, 1, "Frostschwert", new ColoredChar('t', Color.CYAN), holder, 50);
		this.description = "Fügt zwar keinen Schaden zu, paralysiert aber für 5 Runden.";
	}


	@Override
	public void shoot(Creature attacker, Creature victim){
		if (ammoLeft > 0)
			reduceAmmo();
		victim.addGeneralFeature(new Paralyzed(5, victim));
	}
	
	@Override
	public String getEquipText() {
		return "Das Schwert fühlt sich kalt an.";
	}
	
	@Override
	protected String getPickupText() {
		return "Das Schwert flüstert: \"Lass uns Frostriesen jagen gehen!\"";
	}

}
