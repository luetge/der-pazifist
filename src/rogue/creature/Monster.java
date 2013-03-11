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
    }

	@Override
	public void walk() {
		if(!neutralized)
			move(Dice.global.choose(Arrays.asList(Direction.values())));
	}
}
