package rogue.creature;

import jade.util.Dice;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Direction;

import java.util.Arrays;

public class Monster extends Creature
{
    public Monster(ColoredChar face, String Name)
    {
        super(face, Name);
        min_d = 10;
        max_d = 24;
    }

	@Override
	public void walk() {
		if(!neutralized)
			move(Dice.global.choose(Arrays.asList(Direction.values())));
	}
}
