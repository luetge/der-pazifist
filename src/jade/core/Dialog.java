package jade.core;
import jade.ui.TiledTermPanel;
import jade.ui.View;
import jade.util.Guard;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Direction;
import java.awt.Color;
import java.io.IOException;

import rogue.creature.Ally;
import jade.ui.Log;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileReader;

public class Dialog {
	private abstract class Node {
		private int id;
		Node (int id) {
			this.id = id;
		}
		public final int getID() {
			return id;
		}
		public abstract int tick(World world, TiledTermPanel term, Ally speaker);
		public abstract void load(BufferedReader reader);
		public ArrayList<String> loadText (BufferedReader reader)
		{
			ArrayList<String> list = new ArrayList<String> ();
			try {
			String str;
			while ((str = reader.readLine()) != null)
			{
				if (str.equals(id + ":done"))
					return list;
				list.add(str);
			}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return list;
		}
	}
	private class GotoNode extends Node {
		private int destination;
		public GotoNode(int id, String args[])
		{
			super(id);
			Guard.validateArgument(args.length == 3);
			destination = Integer.parseInt(args[2]);
		}
		
		@Override
		public int tick (World world, TiledTermPanel term, Ally speaker)
		{
			return destination;
		}
		
		@Override
		public void load (BufferedReader reader)
		{
		}
	}
	private class TextNode extends Node {
		private ArrayList<String> text;
		private String speakeroverride;
		public TextNode(int id, String args[])
		{
			super(id);
			if (args.length > 2)
				speakeroverride = new String(args[2]);
			else
				speakeroverride = null;
		}
		
		@Override
		public int tick (World world, TiledTermPanel term, Ally speaker)
		{
			View.global().update(term, world);
			
			Dialog.say(term, speakeroverride == null ? speaker.getName() : speakeroverride,
					text);
			Dialog.waitforspace(term);
			return getID() + 1;
		}
		
		@Override
		public void load (BufferedReader reader)
		{
			text = loadText (reader);
		}
	}
	private class GiveItemNode extends Node {
		String type;
		int amount;
		public GiveItemNode(int id, String args[])
		{
			super(id);
			Guard.validateArgument(args.length == 4);
			type = new String (args[2]);
			amount = Integer.parseInt(args[3]);
		}
		
		@Override
		public int tick (World world, TiledTermPanel term, Ally speaker)
		{
			Log.addMessage("Der Pazifist gibt " + speaker.getName() + " " + amount
					+ " " + type + ".");
			return getID() + 1;
		}
		
		@Override
		public void load (BufferedReader reader)
		{
		}
	}
	private class ReceiveItemNode extends Node {
		String type;
		int amount;
		public ReceiveItemNode(int id, String args[])
		{
			super(id);
			Guard.validateArgument(args.length == 4);
			type = new String (args[2]);
			amount = Integer.parseInt(args[3]);
		}
		
		@Override
		public int tick (World world, TiledTermPanel term, Ally speaker)
		{
			Log.addMessage("Der Pazifist erhält " + amount
					+ " " + type + " von " + speaker.getName() + ".");
			return getID() + 1;
		}
		
		@Override
		public void load (BufferedReader reader)
		{
		}
	}
	private class QuestionNode extends Node {
		private ArrayList<String> text;
		private String choices[];
		private int destinations[];
		public QuestionNode(int id, String args[])
		{
			super(id);
			Guard.validateArgument((args.length&1)==0);
			choices = new String[(args.length>>1)-1];
			destinations = new int[(args.length>>1)-1];
			for (int i = 1; i < (args.length>>1); i++)
			{
				choices[i-1] = new String (args[i*2]);
				destinations[i-1] = Integer.parseInt(args[i*2+1]);
			}
		}
		
		@Override
		public int tick (World world, TiledTermPanel term, Ally speaker)
		{
			View.global().update(term, world);

			if (text.size() > 0)
				Dialog.say(term, speaker.getName(), text, -choices.length/2-1);

			int width = Dialog.getMaxWidth(choices);
			int height = text.size();
			int posx = term.width()/2 - width/2;
			int posy = term.height()/2 + height/2 + choices.length/2 + 1;
			if (text.size() == 0)
				posy -= choices.length;
			
			int currentchoice = 0;

			Dialog.clearRect(term, posx-1, posy, width+2, destinations.length);
			try {
				while (true)
				{
					for (int i = 0; i < choices.length; i++)
					{
						Color color = Color.white;
						if (i == currentchoice)
							color = Color.yellow;
				
						term.bufferString(posx, posy+i, choices[i], color);
				
					}
					term.refreshScreen();
				
					int key = term.getKey();
					if (key == ' ')
						break;
					Direction dir = Direction.keyToDir(key);
					if (dir != null)
					{
						if (dir == Direction.NORTH)
						{
							currentchoice--;
							if (currentchoice < 0)
								currentchoice = 0;
						}
						else
						{
							currentchoice++;
							if (currentchoice >= choices.length)
								currentchoice = choices.length - 1;
						}
					}
				}
				return destinations[currentchoice];
			} catch (InterruptedException e) {
				e.printStackTrace();
				return -1;
			}
		}
		@Override
		public void load (BufferedReader reader)
		{
			text = loadText (reader);
			return;
		}
	}
	private class EndNode extends Node {
		int returnval;
		public EndNode(int id, String args[])
		{
			super(id);
			returnval = Integer.parseInt(args[2]);
		}
		
		@Override
		public int tick (World world, TiledTermPanel term, Ally speaker)
		{
			return returnval;
		}
		@Override
		public void load (BufferedReader reader)
		{
			return;
		}
	}
	private ArrayList<Node> nodes;
	private Ally speaker;
	private int currentid;
	public Dialog (String filename) {
		this.nodes = new ArrayList<Node> ();
		this.currentid = 0;
		load (filename);
	}
	
	void load (String filename)
	{
		try {
			BufferedReader reader = new BufferedReader (new FileReader (filename));

			String str;
			while((str = reader.readLine()) != null)
			{
				String args[] = str.split(":");
				
				Guard.validateArgument(args.length >= 2);
					
				int id = Integer.parseInt(args[0]);
				Guard.validateArgument(id >= 0);
				
				Node node;
				
				if (args[1].equals("text"))
				{
						node = new TextNode (id, args);
				} else if (args[1].equals("question")) {
						node = new QuestionNode (id, args);
				} else if (args[1].equals("end")) {
						node = new EndNode (id, args);
				} else if (args[1].equals("giveitem")) {
						node = new GiveItemNode (id, args);
				} else if (args[1].equals("receiveitem")) {
						node = new ReceiveItemNode (id, args);
				} else if (args[1].equals("goto")) {
						node = new GotoNode (id, args);
				} else {
					System.out.println("Unknown dialog entry: " + args[1]);
					throw new IllegalStateException();
				}
				
				node.load(reader);
				
				nodes.add(id, node);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setActor (Ally speaker)
	{
		this.speaker = speaker;
	}
	
	public static int getMaxWidth (String strings[])
	{
		int maxlen = 0;
		for (int i = 0; i < strings.length; i++)
		{
			int len = strings[i].length(); 
			if (len > maxlen)
				maxlen = len;
		}
		return maxlen;
	}

	public static int getMaxWidth (ArrayList<String> strings)
	{
		int maxlen = 0;
		for (String str : strings)
		{
			if (str.length() > maxlen)
				maxlen = str.length ();
		}
		return maxlen;
	}
	
	public static void clearRect (TiledTermPanel term, int posx, int posy,
			int width, int height)
	{
		for (int y = posy; y < posy + height; y++)
		{
			for (int x = posx; x < posx + width; x++)
			{
				term.bufferBackground(x, y, Color.black);
				term.unbuffer(x,y);
				term.bufferTile(x,y,ColoredChar.create(' '));
			}
		}
	}
	
	public static void say (TiledTermPanel term, String speaker, ArrayList<String> t)
	{
		say (term, speaker, t, 0);
	}

	public static void say (TiledTermPanel term, String speaker, ArrayList<String> t, int dy)
	{
		int width = getMaxWidth(t) + speaker.length() + 2;
		int height = t.size();
		int posx = term.width()/2 - width/2;
		int posy = term.height()/2 - height/2 + dy;
		
		clearRect (term, posx-1, posy-1, width+2, height+2);
		
		for (int i = 0; i < t.size(); i++)
		{
			term.bufferString(term.width()/2 - width/2, posy + i, speaker + ": ",
					Color.green);
			term.bufferString(term.width()/2 - width/2 + speaker.length() + 2,
					posy + i, t.get(i));
			Log.addMessage(speaker + ": " + t.get(i));
		}
	}
	
	public static void waitforspace (TiledTermPanel term)
	{
		try {
			do {
				term.refreshScreen();
			} while (term.getKey() != ' ');
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void tick (World world, TiledTermPanel term)
	{ 
		Guard.argumentIsNotNull(speaker);
		Guard.argumentInsideBound(currentid, nodes.size());
		
		Node node;
		do
		{
			node = nodes.get(currentid);
			currentid = node.tick(world, term, speaker);
		} while (!node.getClass().isAssignableFrom(EndNode.class));
		
		world.setActiveDialog(null);
	}
}
