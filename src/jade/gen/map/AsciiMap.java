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

import jade.ui.TermPanel;
import jade.util.datatype.MutableCoordinate;
import jade.util.datatype.Coordinate;
import jade.util.datatype.ColoredChar;
import jade.util.Guard;
import java.awt.Color;
import jade.core.World;

public class AsciiMap {
	
	private int width, height;
	private Map<Coordinate, ColoredChar> characters;
	private Map<Coordinate, Color> backgrounds;
	private Map<String, Set<Coordinate>> specials;
	public AsciiMap(String filename)
	{
		this();
		loadFromFile(filename);
	}
	
	private AsciiMap()
	{
		characters = new HashMap<Coordinate, ColoredChar> ();
		backgrounds = new HashMap<Coordinate, Color> ();
		specials = new HashMap<String, Set<Coordinate>>();
		width = height = -1;
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
        	world.setTile(characters.get(coord), false, posx + coord.x(), posy + coord.y(), true);
        }
        for (Coordinate coord : backgrounds.keySet())
        {
        	world.setTileBackground(backgrounds.get(coord),  posx + coord.x(), posy + coord.y());
        }
	}
	
	public void render (TermPanel term)
	{
		for (Coordinate coord : characters.keySet())
			term.bufferChar(coord,  characters.get(coord));
		for (Coordinate coord : backgrounds.keySet())
			term.bufferBackground(coord, backgrounds.get(coord));
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

		private void processEscape (Coordinate coord, String esc)
		{
			if(esc.startsWith("bg:"))
			{
				background = Color.decode("0x"+esc.substring(3));
			}
			else if (esc.startsWith("fg:"))
			{
				foreground = Color.decode("0x"+esc.substring(3));
			}
			else
			{
				Set<Coordinate> set = specials.get(esc);
				if (set == null)
					specials.put(esc, set = new HashSet<Coordinate> ());
				set.add(coord);
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
				characters.put(coord.copy(), new ColoredChar (c, foreground));
				if (width < coord.x())
					width = coord.x();
				if (height < coord.y())
					height = coord.y();
				if (!background.equals(Color.black))
				{
					backgrounds.put(coord.copy(), background.brighter());
					if (width < coord.x())
						width = coord.x();
					if (height < coord.y())
						height = coord.y();
				}
				coord.setXY(coord.x()+1,coord.y());
			}
			coord.setXY(0,coord.y()+1);
		}
	
		public void load(Reader r)
		{
			try {
				BufferedReader reader = new BufferedReader(r);
				String str;
				foreground = Color.white;
				background = Color.black;
				MutableCoordinate coord = new MutableCoordinate (0,0);
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
