package rogue.creature;

import java.util.Arrays;

import pazi.features.Walking;
import jade.util.Dice;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Direction;

public class Monster extends Creature
{
    public Monster(ColoredChar face, String Name)
    {
        super(face, Name);
        features.add(new Walking());
    }

	@Override
	public void walk() {
        move(Dice.global.choose(Arrays.asList(Direction.values())));
	}
}
