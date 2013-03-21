package rogue.creature;

import jade.core.World;
import jade.util.Dice;
import jade.util.datatype.ColoredChar;

import java.awt.Color;

import pazi.trigger.CreatureTrigger;
import pazi.trigger.TriggerFactory;

public class JokesTeller extends Creature {
	
	protected static final String[] msgs = new String[] { 	"...Ist mir scheißegal, wer Dein Vater ist! Solange ich am Angeln bin läuft hier niemand übers Wasser!",
													"Wie nennt man die Fussballschuhe von Jesus? Christstollen!",
													"Warum war Jesus ein typischer Student? Er wohnte bis 30 noch bei seiner Mutter, hatte lange Haare und wenn er was gearbeitet hat, war es ein Wunder.",
													"Wer war der erste Basketballer? Jesus! Schließlich steht in der Bibel: Er nahm das Brot und dunkte...",
													"Was hat Jesus am Karfreitag gesagt? \"Also dann, schönes Wochenende!\"",
													"Was ist der Unterschied zwischen Jesus Christus und Casanova? Der Gesichtsausdruck beim nageln!",
													"Wer war der erste Verkehrssünder? Jesus, denn er hatte zwölf Anhänger!",
													"Wer erfand das Deo? Jesus, er brach das Brot und verteilte es unter den Armen." };

	public JokesTeller(World world) {
		super(new ColoredChar('O', Color.magenta), "Witzbold");
		CreatureTrigger ct = (CreatureTrigger)TriggerFactory.createTrigger("jokestrigger", world);
		ct.attach(this);
		world.getTrigger().add(ct);
	}
	
	public static String getRandomMessage(){
		return msgs[Dice.global.nextInt(msgs.length-1)];
	}
}
