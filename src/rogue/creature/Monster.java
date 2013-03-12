package rogue.creature;

import jade.path.AStar;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;

public class Monster extends Creature
{
	protected static AStar pathFinder = new AStar();
	protected Coordinate nextCoordinate;
	
    public Monster(ColoredChar face, String Name)
    {
        super(face, Name);
        min_d = 0;
        max_d = 5;
    }

	@Override
	public void walk() {
		if(!neutralized && nextCoordinate != null)
				setPos(nextCoordinate);		
	}

	public void setNextCoord(Coordinate coordinate) {
		nextCoordinate = coordinate;
	}
}
