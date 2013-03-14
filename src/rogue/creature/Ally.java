package rogue.creature;

import jade.util.datatype.ColoredChar;
import jade.core.Dialog;

public class Ally extends Creature {

	public Ally(ColoredChar face, String Name) {
		this(new ColoredChar[]{face,face,face,face,face,face,face,face,face}, Name);
		setPassable (false);
		// TODO Auto-generated constructor stub
	}

	public Ally(ColoredChar[] faces, String Name) {
		super(faces, Name);
		// TODO Auto-generated constructor stub
	}

	public void startDialog ()
	{
		world().setActiveDialog (new Dialog ());
	}
}
