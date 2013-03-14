package rogue.creature;

import jade.path.AStar;
import jade.util.datatype.ColoredChar;
import pazi.features.DefaultFightBehaviour;
import pazi.features.EatBrains;

public class Monster extends Creature
{
	protected static AStar pathFinder = new AStar();
	
	public Monster(ColoredChar faces[], String Name)
	{
		super(faces, Name);
        min_d = 0;
        max_d = 5;
        setFightBehaviour(new DefaultFightBehaviour());
        getWalkFeatures().add(new EatBrains());
	}
	
    public Monster(ColoredChar face, String Name)
    {
    	this (new ColoredChar[]{face, face, face, face, face, face, face, face, face }, Name);
    }
}
