package jade.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jade.util.Guard;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFrame;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import jade.core.World;
import jade.gen.map.AsciiMap;
import rogue.creature.Player;


public abstract class View {
	
	private static View view = null;
	
	public abstract int tileHeight();
	
	public abstract int tileWidth();
	
	public abstract JFrame getFrame();
	
	public abstract void setCenter (Coordinate center);
	
	public abstract void setCenter (int x, int y);
	
	public abstract void loadTiles();
	
	public abstract int columns();
	
	public abstract int rows();
	
	public abstract void clearTiles();
	
	public abstract boolean nextKey ();
	
	public abstract int getKeyEvent();
	
	public static View get ()
	{
		return view;
	}
	
	public static void set(View view)
	{
		View.view = view;
	}
	
 
/*	public static void create (String title)
=======
	public void setCenter (Coordinate center)
	{ 
		this.center_x = center.x();
		this.center_y = center.y();
	}
	
	public void setCenter (int x, int y)
	{
		this.center_x = x;
		this.center_y = y;
	}
	
	public void loadTiles(String dirname)
	{
		File tiledir = new File(dirname);
		Guard.verifyState(tiledir.isDirectory());
		File dirs[] = tiledir.listFiles();
		
		for (File dir : dirs)
		{
			if (!dir.isDirectory())
				continue;
			File files[] = dir.listFiles(new FilenameFilter() {
				public boolean accept (File dir, String name) {
					return name.endsWith(".txt");
				}	
			});	
			for (File file : files)
			{
				String filename = file.getPath();
				filename = filename.substring(0, filename.length()-4) + ".png";
			
				try {
					BufferedReader reader; 
					reader = new BufferedReader(new FileReader (file));
					String ch = reader.readLine();
					String color = reader.readLine();
					reader.close();
				
					Guard.argumentsAreNotNull(ch, color);
				
					gltiles.put(ColoredChar.create(ch.charAt(0), Color.decode("0x"+color)), new GLTile (filename));
				
				} catch(FileNotFoundException e) {
					e.printStackTrace();
					continue;
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}
	
	public int columns()
	{
		return columns;
	}
	
	public int rows()
	{
		return rows;
	}
	
	public void clearTiles()
	{
		gltiles.clear();
	}
	
    static Map<Integer, Integer> keytranslatemap = createKeyTranslateMap();
    
    private static Map<Integer, Integer> createKeyTranslateMap()
    {
    	Map<Integer, Integer> map = new HashMap<Integer, Integer> ();
    	map.put(Keyboard.KEY_SPACE, (int) ' ');
    	map.put(Keyboard.KEY_H, (int) 'H');
    	map.put(Keyboard.KEY_LEFT, KeyEvent.VK_LEFT);
    	map.put(Keyboard.KEY_RIGHT, KeyEvent.VK_RIGHT);
    	map.put(Keyboard.KEY_UP, KeyEvent.VK_UP);
    	map.put(Keyboard.KEY_DOWN, KeyEvent.VK_DOWN);
    	map.put(Keyboard.KEY_ESCAPE, KeyEvent.VK_ESCAPE);
    	return map;
    }
	
	public boolean nextKey ()
	{
		boolean state = Keyboard.next();
		while (state && !Keyboard.getEventKeyState())
		{
			state = Keyboard.next();
		}
		return state;
	}
	
	public int getKeyEvent()
	{
    	Integer event = keytranslatemap.get(Keyboard.getEventKey());
    	if (event == null)
    		return -1;
    	return event;
	}
	
	public static void create (String title)
 	{
		create (title, 90, 27, 10, 16);
	}
	
	public static void create (String title, int columns, int rows, int tileWidth, int tileHeight)
	{
		view = new View (title, columns, rows, tileWidth, tileHeight);
	}
	
	public static View get ()
	{
		return view;
	}*/
	
	public abstract void clear ();
	
	public abstract void displayScreen (AsciiMap screen);
	
	public abstract boolean closeRequested ();
	
	public abstract void drawBackground (Coordinate coord, Color background);
	
	public abstract void drawBackground (int x, int y, Color background);
	
	public abstract void drawTile (Coordinate coord, ColoredChar ch);
	
	public abstract void drawTile (float x, float y, float z, ColoredChar ch);
	
	public abstract void drawChar (Coordinate coord, ColoredChar ch);
	
	public abstract void drawChar (float x, float y, float z, ColoredChar ch);
	
	public abstract void drawString (float x, float y, float z, String string, Color color);
	
	public abstract void update ();
	
	public abstract boolean wasResized ();
	
	public abstract void drawWorld (World world);
}
