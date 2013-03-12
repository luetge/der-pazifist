package jade.util.datatype;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

public class Door {
	private String id;
	private String destworld;
	private String destid;
	private Coordinate position;
	private Direction direction;
	public Door(String id, Coordinate position, String destworld, String destid)
	{
		this(id, position, destworld, destid, Direction.NORTH);
	}
	public Door(String id, Coordinate position, String destworld, String destid, Direction direction)
	{
		this.id = id;
		this.destworld = destworld;
		this.destid = destid;
		this.position = position;
		this.direction = direction;
	}
	public Door(String id, int posx, int posy, String destworld, String destid)
	{
		this(id, posx, posy, destworld, destid, Direction.NORTH);
	}

	public Door(String id, int posx, int posy, String destworld, String destid, Direction direction)
	{
		this(id, new Coordinate(posx, posy), destworld, destid, direction);
	}
	
	public String getID()
	{
		return id;
	}
	
	public String getDestWorld()
	{
		return destworld;
	}
	public String getDestID()
	{
		return destid;
	}
	
	public Coordinate getPosition()
	{
		return position;
	}
	public Direction getDirection()
	{
		return direction;
	}
}
