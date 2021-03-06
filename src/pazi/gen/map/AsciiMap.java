package pazi.gen.map;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Reader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.awt.Color;

import org.newdawn.slick.util.ResourceLoader;

import pazi.core.Actor;
import pazi.core.World;
import pazi.creature.CreatureFactory;
import pazi.items.Item;
import pazi.items.ItemFactory;
import pazi.trigger.ITrigger;
import pazi.trigger.TriggerFactory;
import pazi.ui.View;
import pazi.util.Dice;
import pazi.util.Guard;
import pazi.util.datatype.ColoredChar;
import pazi.util.datatype.Coordinate;
import pazi.util.datatype.Direction;
import pazi.util.datatype.Door;
import pazi.util.datatype.MutableCoordinate;


public class AsciiMap {
	
	private class Tile
	{
		private boolean passable;
		ColoredChar ch;
		
		Tile(ColoredChar ch, boolean passable)
		{
			this.ch = ch;
			this.passable = passable;
		}
		
		ColoredChar ch ()
		{
			return ch;
		}
		
		boolean passable ()
		{
			return passable;
		}
	}
	
	private int width, height;
	private Map<Coordinate, Tile> characters;
	private Map<Coordinate, Color> backgrounds;
	private Map<Coordinate, Door> doors;
	private Map<Coordinate, String> creatures;
	private Map<Coordinate, String> triggers;
	private Map<Coordinate, String> items;
	private Map<String, Set<Coordinate>> specials;
	public AsciiMap(String filename)
	{
		this();
		loadFromFile(filename);
	}
	
	private AsciiMap()
	{
		creatures = new HashMap<Coordinate, String>();
		triggers = new HashMap<Coordinate, String>();
		items = new HashMap<Coordinate, String>();
		characters = new HashMap<Coordinate, Tile> ();
		backgrounds = new HashMap<Coordinate, Color> ();
		specials = new HashMap<String, Set<Coordinate>>();
		doors = new HashMap<Coordinate, Door> ();
		width = height = -1;
	}
	
	public Map<Coordinate, Door> getDoors()
	{
		return doors;
	}
	
	public Map<Coordinate, String> getCreatures()
	{
		return creatures;
	}
	
	public Map<Coordinate, String> getTriggers()
	{
		return triggers;
	}
	
	public Map<Coordinate, String> getItems()
	{
		return items;
	}
	
	private void loadFromFile (String filename)
	{
		Loader l = new Loader();
		try {
			l.load(new InputStreamReader (ResourceLoader.getResourceAsStream(filename), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private void loadFromString (String str)
	{
		Loader l = new Loader();
		l.load(new StringReader(str));
	}
	
	public static AsciiMap createFromString(String str)
	{
		AsciiMap asciimap = new AsciiMap();
		asciimap.loadFromString(str);
		return asciimap;
	}
	
	public void render (World world, int posx, int posy)
	{
        for (Coordinate coord : characters.keySet())
        {
        	world.setTile(characters.get(coord).ch(), characters.get(coord).passable(),
        			posx + coord.x(), posy + coord.y(), true);
        }
        for (Coordinate coord : backgrounds.keySet())
        {
        	world.setTileBackground(backgrounds.get(coord),  posx + coord.x(), posy + coord.y());
        }
	}
	public void addCreatures (World world)
	{
        for (Coordinate coord : creatures.keySet())
        {
        	world.addActor(CreatureFactory.createCreature(creatures.get(coord), world), coord);
        }
	}

	public void addItems (World world)
	{
        for (Coordinate coord : items.keySet())
        {
        	Item item = ItemFactory.createItem(items.get(coord));
        	world.addActor(item, coord);
        }
	}

	public void addTriggers (World world)
	{
        for (Coordinate coord : triggers.keySet())
        {
        	ITrigger trigger = TriggerFactory.createTrigger(triggers.get(coord), world);
        	world.addActor((Actor)trigger, coord);
        	world.getTrigger().add(trigger);
        }
	}

	public void render (View view, Coordinate pos)
	{
		for (Coordinate coord : backgrounds.keySet())
			view.drawBackground(coord.getTranslated(pos), backgrounds.get(coord));
		for (Coordinate coord : characters.keySet())
			view.drawTile(coord.getTranslated(pos),  characters.get(coord).ch());
	}
	
	public void render (View view, int posx, int posy)
	{
		render(view, new Coordinate (posx, posy));
	}
	
	public Set<String> getSpecials ()
	{
		return specials.keySet();
	}
	
	public Set<Coordinate> getSpecialCoords (String name)
	{
		return specials.get(name);
	}
	
	public int width()
	{
		return width;
	}
	
	public int height()
	{
		return height;
	}
	
	private class Loader {
		private Color background;
		private Color foreground;
		private boolean passable;
		private boolean brighten;
		private Map<String, String> aliases;
		private Map<String, Integer> randomnumbers;
		
		private class LineParser
		{
			private String line;
			private int pos;
			public LineParser(String line)
			{
				this.line = line;
				pos = 0;
			}
			private void processEscape (MutableCoordinate coord, String input)
			{
				String esc;
				esc = aliases.get(input);
				if (esc == null)
					esc = input;
				if(esc.startsWith("bg:"))
				{
					background = Color.decode("0x"+esc.substring(3));
				}
				else if (esc.startsWith("fg:"))
				{
					foreground = Color.decode("0x"+esc.substring(3));
				}
				else if (esc.startsWith("creature:"))
				{
					creatures.put(coord.copy(), esc.substring(9));
				}
				else if (esc.startsWith("item:"))
				{
					items.put(coord.copy(), esc.substring(5));
				}
				else if (esc.startsWith("trigger:"))
				{
					triggers.put(coord.copy(), esc.substring(8));
				}
				else if (esc.startsWith("xrandom:"))
				{
					String params[] = esc.substring(8).split(",");
					Guard.verifyState(params.length == 3);
					Integer randomnumber;
					if (params[0].equals("new"))
					{
						randomnumber = Dice.global.nextInt(Integer.parseInt(params[1]),
								Integer.parseInt(params[2]));
					}
					else
					{
						randomnumber = randomnumbers.get(params[0]);
						if (randomnumber == null)
						{
							randomnumber = Dice.global.nextInt(Integer.parseInt(params[1]),
									Integer.parseInt(params[2]));
							randomnumbers.put(params[0], randomnumber);
						}
					}
					String randomend = "\u00a5xrandomend:"+params[0]+"\u00a5"; // ¥
					int sequenceend = line.substring(pos+1).indexOf(randomend);
					for (int i = 0; i < randomnumber; i++)
					{
						LineParser parser = new LineParser (line.substring(pos+1, pos+1+sequenceend));
						parser.execute(coord);
					}
					pos += sequenceend + randomend.length();

				}
				else if (esc.equals("brighten"))
				{
					brighten = true;
				}
				else if (esc.equals("brighten"))
				{
					brighten = true;
				}
				else if (esc.equals("dontbrighten"))
				{
					brighten = false;
				}
				else if (esc.startsWith("door:"))
				{
					String params[] = esc.substring(5).split(",");
					Guard.validateArgument(params.length == 4);
					Direction dir = Direction.NORTH;
					Guard.validateArgument(params[3].length() == 1);
					switch (params[3].charAt(0))
					{
					case 'n':
						dir = Direction.NORTH;
						break;
					case 'w':
						dir = Direction.WEST;
						break;
					case 'e':
						dir = Direction.EAST;
						break;
					case 's':
						dir = Direction.SOUTH;
						break;
					}
					doors.put(coord.copy(), new Door(params[0],
							coord.x(), coord.y(), params[1], params[2], dir));
				}
				else if (esc.equals("p"))
				{
					passable = true;
				}
				else if (esc.equals("np"))
				{
					passable = false;
				}
				else
				{
					Set<Coordinate> set = specials.get(esc);
					if (set == null)
						specials.put(esc, set = new HashSet<Coordinate> ());
					set.add(coord.copy());
				}
			}
	
			private void execute (MutableCoordinate coord)
			{
				for (pos = 0; pos < line.length(); pos++)
				{
					char c = line.charAt(pos);
					if (c == '\u00a5') // ¥
					{
						int escstart = pos+1;
						int escend = line.indexOf('\u00a5', escstart); // ¥
						pos = escend;
						Guard.argumentIsPositive (escend);
						processEscape(coord, line.substring(escstart, escend));
						continue;
					}
					characters.put(coord.copy(), new Tile(new ColoredChar (c, foreground),
							passable));
					if (width < coord.x())
						width = coord.x();
					if (height < coord.y())
						height = coord.y();
					if (!background.equals(Color.black))
					{
						backgrounds.put(coord.copy(), brighten?background.brighter():background);
						if (width < coord.x())
							width = coord.x();
						if (height < coord.y())
							height = coord.y();
					}
					coord.setXY(coord.x()+1,coord.y());
				}
			}
		}
		
		private void parseAliases(String aliases)
		{
			if (aliases.isEmpty())
				return;
			String list[] = aliases.split(";");
			for (String alias : list)
			{
				String s[] = alias.split("=");
				Guard.validateArgument(s.length==2);
				this.aliases.put(s[0],s[1]);
			}
		}
		
		private class BlockParser
		{
			ArrayList<String> block;
			public BlockParser(ArrayList<String> block)
			{
				this.block = block;  
				
			}
			
			public void execute (MutableCoordinate coord)
			{
				for (int i = 0; i < block.size(); i++)
				{
					String str = block.get(i);
					if (str.startsWith("\u00a5yrandom:")) // ¥
					{
						String params[] = str.trim().substring(9, str.length()-1).split(",");
						Guard.verifyState(params.length == 3);
						Integer randomnumber;
						if (params[0].equals("new"))
						{
							randomnumber = Dice.global.nextInt(Integer.parseInt(params[1]),
									Integer.parseInt(params[2]));
						}
						else
						{
							randomnumber = randomnumbers.get(params[0]);
							if (randomnumber == null)
							{
								randomnumber = Dice.global.nextInt(Integer.parseInt(params[1]),
										Integer.parseInt(params[2]));
								randomnumbers.put(params[0], randomnumber);
							}
						}
						
						ArrayList<String> subblock = new ArrayList<String> ();
						String endstr = "\u00a5yrandomend:" + params[0] + "\u00a5"; // ¥
						i++;
						str = block.get(i);
						while (!str.equals(endstr))
						{
							subblock.add(str);
							i++;
							str = block.get(i);
						}
						
						for (int c = 0; c < randomnumber; c++)
							new BlockParser(subblock).execute(coord);

						continue;
					}
					LineParser lineparser = new LineParser(str);
					lineparser.execute(coord);
					coord.setXY(0,coord.y()+1);
				}
			}
		}
	
		public void load(Reader r)
		{
			try {
				BufferedReader reader = new BufferedReader(r);
				String str;
				foreground = Color.white;
				background = Color.black;
				passable = false;
				brighten = false;
				MutableCoordinate coord = new MutableCoordinate (0,0);
				aliases = new HashMap<String,String>();
				randomnumbers = new HashMap<String, Integer> ();
				parseAliases(reader.readLine());
				ArrayList<String> lines = new ArrayList<String>();
				while ((str = reader.readLine ()) != null)
				{
					lines.add(str);
				}
				reader.close();

				new BlockParser(lines).execute(coord);

				width++;
				height++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
