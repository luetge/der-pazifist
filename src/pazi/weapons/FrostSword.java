package pazi.weapons;

import java.awt.Color;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;
import pazi.features.Paralyzed;
import pazi.items.Item;
import rogue.creature.Creature;

public class FrostSword extends MeleeWeaponPrototype {

	public FrostSword(){
		this(null);
	}
	
	public FrostSword(Creature holder) {
		super(0, 0, 1, "Frostschwert", new ColoredChar('t', Color.CYAN), holder);
		this.description = "Fügt zwar keinen Schaden zu, paralysiert aber für 5 Runden.";
	}


	@Override
	public void shoot(Creature attacker, Creature victim){
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
