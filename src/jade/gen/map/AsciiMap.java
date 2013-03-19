package jade.gen.map;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.io.Reader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jade.ui.View;
import jade.util.datatype.MutableCoordinate;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Door;
import jade.util.Guard;
import java.awt.Color;

import rogue.creature.CreatureFactory;
import jade.core.World;

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
	private Map<String, Set<Coordinate>> specials;
	public AsciiMap(String filename)
	{
		this();
		loadFromFile(filename);
	}
	
	private AsciiMap()
	{
		creatures = new HashMap<Coordinate, String>();
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
	
	private void loadFromFile (String filename)
	{
		try {
			Loader l = new Loader();
			l.load(new FileReader(filename));
		} catch (FileNotFoundException e) {
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
	
	public Set<Coordinate> getSpecial (String name)
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

		private void processEscape (Coordinate coord, String input)
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
	
		private void processLine (String line, MutableCoordinate coord)
		{
			for (int i = 0; i < line.length(); i++)
			{
				char c = line.charAt(i);
				if (c == '¥')
				{
					int escend = line.indexOf('¥', i+1);
					Guard.argumentIsPositive (escend);
					processEscape(coord, line.substring(i+1,escend));
					i = escend;
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
			coord.setXY(0,coord.y()+1);
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
				parseAliases(reader.readLine());
				while ((str = reader.readLine ()) != null)
				{
					processLine (str, coord);
				}
				reader.close();
				width++;
				height++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
