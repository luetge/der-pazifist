package pazi.core;
import java.awt.Color;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

import org.newdawn.slick.util.ResourceLoader;

import org.lwjgl.input.Keyboard;

import pazi.creature.Ally;
import pazi.creature.Creature;
import pazi.items.Item;
import pazi.items.ItemFactory;
import pazi.ui.Log;
import pazi.ui.View;
import pazi.util.Guard;
import pazi.util.datatype.Direction;

public class Dialog {
	private abstract class Node {
		private Dialog parent;
		private int id;
		protected Node (Dialog parent, int id) {
			this.parent = parent;
			this.id = id;
		}
		protected Dialog parent ()
		{
			return parent;
		}
		public final int getID() {
			return id;
		}
		public abstract int tick(World world, Creature speaker);
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
	private abstract class StateNode extends Node {
		protected String statename;
		protected StateNode (Dialog parent, int id, String args[])
		{
			super(parent, id);
			Guard.validateArgument(args.length >= 3);
			statename = args[2];
		}
		
		@Override
		public void load (BufferedReader reader)
		{
		}
	}
	private class StateLargerNode extends StateNode {
		private int value;
		private int yes_destination;
		private int no_destination;
		public StateLargerNode (Dialog parent, int id, String args[])
		{
			super (parent, id, args);
			Guard.validateArgument(args.length == 6);
			value = Integer.parseInt(args[3]);
			yes_destination = Integer.parseInt(args[4]);
			no_destination = Integer.parseInt(args[5]);
		}
		
		@Override
		public int tick (World world, Creature speaker)
		{
			if (parent().getState(statename) > value)
				return yes_destination;
			else
				return no_destination;
		}
	}
	private class StateSmallerNode extends StateNode {
		private int value;
		private int yes_destination;
		private int no_destination;
		public StateSmallerNode (Dialog parent, int id, String args[])
		{
			super (parent, id, args);
			Guard.validateArgument(args.length == 6);
			value = Integer.parseInt(args[3]);
			yes_destination = Integer.parseInt(args[4]);
			no_destination = Integer.parseInt(args[5]);
		}
		
		@Override
		public int tick (World world, Creature speaker)
		{
			if (parent().getState(statename) < value)
				return yes_destination;
			else
				return no_destination;
		}
	}
	private class IsStateNode extends StateNode {
		private int value;
		private int yes_destination;
		private int no_destination;
		public IsStateNode (Dialog parent, int id, String args[])
		{
			super (parent, id, args);
			Guard.validateArgument(args.length == 6);
			value = Integer.parseInt(args[3]);
			yes_destination = Integer.parseInt(args[4]);
			no_destination = Integer.parseInt(args[5]);
		}
		
		@Override
		public int tick (World world, Creature speaker)
		{
			if (parent().getState(statename) == value)
				return yes_destination;
			else
				return no_destination;
		}
	}
	private class AddStateNode extends StateNode {
		private int value;
		public AddStateNode (Dialog parent, int id, String args[])
		{
			super (parent, id, args);
			Guard.validateArgument(args.length == 4);
			value = Integer.parseInt(args[3]);
		}
		
		@Override
		public int tick (World world, Creature speaker)
		{
			parent().setState(statename, parent().getState(statename) + value);
			return getID()+1;
		}
	}
	private class SetStateNode extends StateNode {
		private int value;
		public SetStateNode (Dialog parent, int id, String args[])
		{
			super (parent, id, args);
			Guard.validateArgument(args.length == 4);
			value = Integer.parseInt(args[3]);
		}
		
		@Override
		public int tick (World world, Creature speaker)
		{
			parent().setState(statename, value);
			return getID()+1;
		}
	}
	private class HasItemNode extends Node {
		private int yes_destination;
		private int no_destination;
		private int amount;
		private String itemname;
		public HasItemNode (Dialog parent, int id, String args[])
		{
			super(parent, id);
			Guard.validateArgument(args.length == 6);
			itemname = args[2];
			amount = Integer.parseInt(args[3]);
			yes_destination = Integer.parseInt(args[4]);
			no_destination = Integer.parseInt(args[5]);
		}
		
		@Override
		public int tick (World world, Creature speaker)
		{
			if (speaker.getInventory().hasItem (itemname, amount))
				return yes_destination;
			else
				return no_destination;
		}
		
		@Override
		public void load (BufferedReader reader)
		{
		}
	}
	private class PlayerHasItemNode extends HasItemNode {
		public PlayerHasItemNode (Dialog parent, int id, String args[])
		{
			super(parent, id, args);
		}
		
		@Override
		public int tick (World world, Creature speaker)
		{
			return super.tick(world,  world.getPlayer());
		}
	}
	private class GotoNode extends Node {
		private int destination;
		public GotoNode(Dialog parent, int id, String args[])
		{
			super(parent, id);
			Guard.validateArgument(args.length == 3);
			destination = Integer.parseInt(args[2]);
		}
		
		@Override
		public int tick (World world, Creature speaker)
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
		public TextNode(Dialog parent, int id, String args[])
		{
			super(parent, id);
			if (args.length > 2)
				speakeroverride = new String(args[2]);
			else
				speakeroverride = null;
			text = null;
		}

		public TextNode (Dialog parent, int id, String speakeroverride, ArrayList<String> text)
		{
			super (parent, id);
			this.speakeroverride = speakeroverride;
			this.text = text;
		}

		@Override
		public int tick (World world, Creature speaker)
		{
			String speakername;
			if (speaker != null)
				speakername = speaker.getName();
			else
				speakername = null;
			for (String t : text)
				Log.addMessage((speakeroverride == null ? speakername : speakeroverride) + ": " + t);
			do
			{
				View.get().drawWorld(world);
			
				Dialog.say(speakeroverride == null ? speakername : speakeroverride,
						text);
			}
			while(!Dialog.checkforspace());
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
		public GiveItemNode(Dialog parent, int id, String args[])
		{
			super(parent, id);
			Guard.validateArgument(args.length == 4);
			type = new String (args[2]);
			amount = Integer.parseInt(args[3]);
		}
		
		@Override
		public int tick (World world, Creature speaker)
		{
			// TODO
			String name = ItemFactory.createItem(type).getName();
			Log.addMessage("Der Pazifist gibt " + speaker.getName() + " " + amount
					+ " " + name + ".");
			world.getPlayer().getInventory().giveItem (speaker, type, amount);
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
		public ReceiveItemNode(Dialog parent, int id, String args[])
		{
			super(parent, id);
			Guard.validateArgument(args.length == 4);
			type = new String (args[2]);
			amount = Integer.parseInt(args[3]);
		}
		
		@Override
		public int tick (World world, Creature speaker)
		{
			// TODO
			String name = ItemFactory.createItem(type).getName();
			Log.addMessage("Der Pazifist erhält " + amount
					+ " " + name + " von " + speaker.getName() + ".");
			speaker.getInventory().giveItem(world.getPlayer(), type, amount);
			return getID() + 1;
		}
		
		@Override
		public void load (BufferedReader reader)
		{
		}
	}
	private class SpawnItemNode extends Node {
		String type;
		int amount;
		public SpawnItemNode(Dialog parent, int id, String args[])
		{
			super(parent, id);
			Guard.validateArgument(args.length == 4);
			type = new String (args[2]);
			amount = Integer.parseInt(args[3]);
		}
		
		@Override
		public int tick (World world, Creature speaker)
		{
			// TODO
			String name = ItemFactory.createItem(type).getName();
			if (speaker != null)
				Log.addMessage("Der Pazifist erhält " + amount
						+ " " + name + " von " + speaker.getName() + ".");
			else
				Log.addMessage("Der Pazifist erhält " + amount
						+ " " + name + ".");
				
			if (type.equals("gold"))
			{
				world.getPlayer().getInventory().findGold(amount);
			}
			else
			{
				for (int i = 0; i < amount; i++)
					world.getPlayer().getInventory().addItem(ItemFactory.createItem(type));
			}
			return getID() + 1;
		}
		
		@Override
		public void load (BufferedReader reader)
		{
		}
	}
	private class LoseItemNode extends Node {
		String type;
		int amount;
		public LoseItemNode(Dialog parent, int id, String args[])
		{
			super(parent, id);
			Guard.validateArgument(args.length == 4);
			type = new String (args[2]);
			amount = Integer.parseInt(args[3]);
		}
		
		@Override
		public int tick (World world, Creature speaker)
		{
			int lost = 0;
			// TODO
			String name = ItemFactory.createItem(type).getName();
			if (type.equals("gold"))
			{
				lost = world.getPlayer().getInventory().loseGold(amount);
			}
			else
			{
				lost = world.getPlayer().getInventory().removeItems(type, amount);
			}
			if (lost > 1)
			{
				Log.addMessage("Der Pazifist verliert " + lost
						+ " " + name + ".");
			}
			else if (lost > 0)
			{
				Log.addMessage("Der Pazifist verliert " + name + ".");				
			}
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
		public QuestionNode(Dialog parent, int id, String args[])
		{
			super(parent, id);
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
		public int tick (World world, Creature speaker)
		{
			View view = View.get();
		
			int width = Dialog.getMaxWidth(choices);
			int height = text.size();
			int posx = view.columns()/2 - width/2;
			int posy = view.rows()/2 + height/2 + choices.length/2 + 1;
			if (text.size() == 0)
				posy -= choices.length;
			
			int currentchoice = 0;
			
			boolean answerselected = false;
			
			if (text.size() > 0)
			{
				for (String t : text)
					Log.addMessage(speaker.getName() + ": " + t);
			}
			
			while (!answerselected)
			{
				view.drawWorld(world);

				if (text.size() > 0)
					Dialog.say(speaker.getName(), text, -choices.length/2-1);

				Dialog.clearRect(posx-1, posy-1, width+2, destinations.length+2);

				for (int i = 0; i < choices.length; i++)
				{
					Color color = Color.white;
					if (i == currentchoice)
						color = Color.yellow;
				
					view.drawString(posx, posy+i, 1.0f, choices[i], color);
				}

				view.update();
				while (view.nextKey())
				{
					int key = view.getKeyEvent();
					if (key == Keyboard.KEY_SPACE)
					{
						answerselected = true;
						break;
					}
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
			}
			Log.addMessage(world.getPlayer().getName() + ": " + choices[currentchoice]);
			return destinations[currentchoice];
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
		public EndNode(Dialog parent, int id, String args[])
		{
			super(parent, id);
			returnval = Integer.parseInt(args[2]);
		}
		
		public EndNode (Dialog parent, int id, int returnval)
		{
			super(parent, id);
			this.returnval = returnval;
		}
		
		@Override
		public int tick (World world, Creature speaker)
		{
			return returnval;
		}
		@Override
		public void load (BufferedReader reader)
		{
			return;
		}
	}
	private Map<Integer, Node> nodes;
	private Map<String, Integer> state;
	private Ally speaker;
	private int currentid;
	public Dialog (String filename) {
		this();
		load (filename);
	}
	private Dialog () {
			this.nodes = new HashMap<Integer, Node> ();
			this.state = new HashMap<String, Integer> ();
			this.currentid = 0;
	}
	
	public void setState (String name, int value)
	{
		state.put(name,  value);
	}
	
	public int getState (String name)
	{
		Guard.verifyState(state.containsKey(name));
		return state.get(name);
	}
	
	public static Dialog createSimpleTextDialog (String speaker, String line)
	{
		ArrayList<String> text = new ArrayList<String> ();
		text.add(line);
		return createSimpleTextDialog(speaker, text);
	}
	
	public static Dialog createSimpleTextDialog (String speaker, ArrayList<String> text)
	{
		Dialog dialog = new Dialog();
		dialog.nodes.put(0, dialog.new TextNode(dialog, 0, speaker, text));
		dialog.nodes.put(1, dialog.new EndNode(dialog, 1, 0));
		return dialog;
	}
	
	void load (String filename)
	{
		try {
			BufferedReader reader = new BufferedReader (new InputStreamReader (ResourceLoader.getResourceAsStream(filename), "UTF-8"));

			String str;
			while((str = reader.readLine()) != null)
			{
				str = str.trim();
				if (str.isEmpty() || str.startsWith("#"))
					continue;
				String args[] = str.split(":");
				
				Guard.validateArgument(args.length >= 2);
					
				int id = Integer.parseInt(args[0]);
				if (id < currentid)
					currentid = id;
				
				Node node;
				
				if (args[1].equals("text"))
				{
					node = new TextNode (this, id, args);
				} else if (args[1].equals("question")) {
					node = new QuestionNode (this, id, args);
				} else if (args[1].equals("end")) {
					node = new EndNode (this, id, args);
				} else if (args[1].equals("giveitem")) {
					node = new GiveItemNode (this, id, args);
				} else if (args[1].equals("receiveitem")) {
					node = new ReceiveItemNode (this, id, args);
				} else if (args[1].equals("spawnitem")) {
					node = new SpawnItemNode (this, id, args);
				}  else if (args[1].equals("loseitem")) {
					node = new LoseItemNode (this, id, args);
				} else if (args[1].equals("goto")) {
					node = new GotoNode (this, id, args);
				} else if (args[1].equals("hasitem")) {
					node = new HasItemNode (this, id, args);
				} else if (args[1].equals("playerhasitem")) {
					node = new PlayerHasItemNode (this, id, args);
				} else if (args[1].equals("setstate")) {
					node = new SetStateNode (this, id, args);
				} else if (args[1].equals("isstate")) {
					node = new IsStateNode (this, id, args);
				} else if (args[1].equals("statelarger")) {
					node = new StateLargerNode (this, id, args);
				} else if (args[1].equals("statesmaller")) {
					node = new StateSmallerNode (this, id, args);
				} else if (args[1].equals("addstate")) {
					node = new AddStateNode (this, id, args);
				} else {
					System.out.println("Unknown dialog entry: " + args[1]);
					throw new IllegalStateException();
				}
				
				node.load(reader);
				
				nodes.put(id, node);
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
	
	public static void clearRect (int posx, int posy, int width, int height)
	{
		Color color = new Color(0, 0, 0, 200);
		for (int y = posy; y < posy + height; y++)
		{
			for (int x = posx; x < posx + width; x++)
			{
				View.get().drawBackground(x, y, color);
			}
		}
	}
	
	public static void say (String speaker, ArrayList<String> t)
	{
		say (speaker, t, 0);
	}
	
	public static void sayTop (String speaker, ArrayList<String> t)
	{
		say (speaker, t, t.size()/2 - View.get().rows()/2 + 2);
	}

	public static void sayTop (String speaker, ArrayList<String> t, int dy)
	{
		say (speaker, t, t.size()/2 - View.get().rows()/2 + dy);
	}

	public static void say (String speaker, ArrayList<String> t, int dy)
	{
		int speakerlen = 0;
		if (speaker != null && speaker.equals("nospeaker"))
			speaker = null;
		if (speaker != null)
			speakerlen = speaker.length() + 2;
		View view = View.get();
		int width = getMaxWidth(t) + speakerlen;
		int height = t.size();
		int posx = view.columns()/2 - width/2;
		int posy = view.rows()/2 - height/2 + dy;
		
		clearRect (posx-1, posy-1, width+2, height+2);
		
		for (int i = 0; i < t.size(); i++)
		{
			if (speaker != null && !speaker.trim().isEmpty())
			{
				if (i == 0)
					view.drawString(view.columns()/2 - width/2, posy + i, 1.0f, speaker + ": ",
							Color.green);
			}
			view.drawString(view.columns()/2 - width/2 + speakerlen,
					posy + i, 1.0f, t.get(i), Color.white);
		}
	}
	
	public static boolean checkforspace ()
	{
		View.get().update();
		while (View.get().nextKey())
		{
			if (View.get().getKeyEvent() == Keyboard.KEY_SPACE)
			{
				return true;
			}
		}
		return false;
	}
	
	public void tick (World world)
	{ 
		Guard.verifyState(nodes.containsKey(currentid));
		
		Node node;
		do
		{
			node = nodes.get(currentid);
			currentid = node.tick(world, speaker);
		} while (!node.getClass().isAssignableFrom(EndNode.class));
		
		world.setActiveDialog(null);
	}
}
