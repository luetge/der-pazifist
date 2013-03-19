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
