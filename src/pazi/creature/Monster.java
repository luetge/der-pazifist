package pazi.creature;

import pazi.path.AStar;
import pazi.util.datatype.ColoredChar;

public class Monster extends Creature
{
	protected static AStar pathFinder = new AStar();
	
	public Monster(ColoredChar faces[], String Name)
	{
		super(faces, Name);
        min_d = 0;
        max_d = 5;
        //getWalkFeatures().add(new EatBrains());
	}
	
    public Monster(ColoredChar face, String Name)
    {
    	this (new ColoredChar[]{face, face, face, face, face, face, face, face, face }, Name);
    }
}
