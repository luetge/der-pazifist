package jade.core;
import jade.util.datatype.Coordinate;

public class Door {
	private String worldname;
	private Coordinate coord;
	public Door(String worldname, Coordinate coord)
	{
		this.worldname = worldname;
		this.coord = coord;
	}
	public Door(String worldname, int x, int y)
	{
		this(worldname, new Coordinate(x,y));
	}
	
	public String getWorldName()
	{
		return worldname;
	}
	public Coordinate getCoord()
	{
		return coord;
	}
}
