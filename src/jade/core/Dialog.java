package jade.core;
import jade.ui.TiledTermPanel;
import jade.util.Guard;
import jade.util.datatype.ColoredChar;
import java.awt.Color;
import rogue.creature.Ally;

public class Dialog {
	private String text;
	private Ally speaker;
	public Dialog () {
		this.text = "Üääähhh... Ich bin hohl...";
	}
	public Dialog (String text) {
		this.text = text;
	}
	public void setActor (Ally speaker)
	{
		this.speaker = speaker;
	}
	
	public void say (TiledTermPanel term, String t)
	{
		int posx = term.width()/2 - text.length()/2;
		int posy = term.height()/2;
		for (int y = posy - 1; y <= posy + 1; y++)
		{
			for (int x = posx - 1; x <= posx + t.length() +  1; x++)
			{
				term.bufferBackground(x, y, Color.black);
				term.unbuffer(x,y);
				term.bufferTile(x,y,ColoredChar.create(' '));
			}
		}
		term.bufferString(posx, posy, t);
	}
	
	public void tick (World world, TiledTermPanel term) throws InterruptedException
	{ 
		Guard.argumentIsNotNull(speaker);
		say (term, speaker.getName() + ": " + text);
		
		do {
			term.refreshScreen();
		} while (term.getKey() != ' ');
		
		world.setActiveDialog(null);
	}
}
