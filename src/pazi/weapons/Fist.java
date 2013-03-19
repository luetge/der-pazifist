package pazi.weapons;

import java.awt.Color;

import jade.util.datatype.ColoredChar;
import rogue.creature.Creature;

public class Fist extends MeleeWeaponPrototype {
	public Fist(Creature holder) { super(20, 60, 1, "Faust", new ColoredChar(' ', Color.black), holder); }
}
