package rogue.creature;

import jade.util.datatype.ColoredChar;
import jade.core.Dialog;

public class Ally extends Creature {
	Dialog dialog;

	public Ally(ColoredChar face, String Name, Dialog dialog) {
		this(new ColoredChar[]{face,face,face,face,face,face,face,face,face}, Name, dialog);
	}

	public Ally(ColoredChar[] faces, String Name, Dialog dialog) {
		super(faces, Name);
		setPassable (false);
		this.dialog = dialog;
		dialog.setActor(this);
	}

	public void startDialog ()
	{
		world().setActiveDialog (dialog);
	}
}
