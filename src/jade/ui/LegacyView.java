package jade.ui;

import jade.core.World;
import jade.gen.map.AsciiMap;
import jade.util.Guard;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.util.ResourceLoader;

import rogue.creature.Player;

public class LegacyView extends View {
	
	private int center_x;
	private int center_y;
	private int columns;
	private int rows;
	private int tileWidth;
	private int tileHeight;
	private boolean closeRequested;
	private JFrame frame;
	private Screen screen;
	private Font font;
	Map<Coordinate, Color> backgroundbuffer;
	Map<Coordinate, ColoredChar> charbuffer;
	Map<Coordinate, ColoredChar> tilebuffer;
	
	private class Screen extends JPanel implements KeyListener
	{
		private BlockingQueue<Integer> inputBuffer;
		private Map<Coordinate, ColoredChar> charbuffer;
		private Map<Coordinate, Color> backgroundbuffer;
		private Map<Coordinate, ColoredChar> tilebuffer;
		private Map<ColoredChar, Image> tileRegister;
		private int tileWidth, tileHeight;
		public Screen (int tileWidth, int tileHeight)
		{
			inputBuffer = new LinkedBlockingQueue<Integer>();
			charbuffer = new HashMap<Coordinate, ColoredChar>();
			backgroundbuffer = new HashMap<Coordinate, Color>();
			tilebuffer = new HashMap<Coordinate, ColoredChar>();
			tileRegister = new HashMap<ColoredChar, Image>();
			addKeyListener(this);
			setFocusable(true);
			this.tileWidth = tileWidth;
			this.tileHeight = tileHeight;
		}
		
		public void registerTile(ColoredChar ch, String filename)
		{
			BufferedImage image;
			try {
				image = ImageIO.read(ResourceLoader.getResourceAsStream(filename));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			synchronized(tileRegister) {
				tileRegister.put(ch, image);
			}
		}
		
		public void clearTiles()
		{
			synchronized(tileRegister) {
				tileRegister.clear();
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
			inputBuffer.offer(e.getKeyCode());
			
		}
		
		public int consumeKey() throws InterruptedException
		{
			return inputBuffer.take();
		}

		@Override
		public void keyReleased(KeyEvent arg0) {}

		@Override
		public void keyTyped(KeyEvent arg0) {}
		
		private void paintChar(Graphics g, int x, int y, ColoredChar ch)
		{
			g.setColor(ch.color());
			g.drawString(ch.toString(), x * tileWidth, (y+1) * tileHeight);
		}
		
		private void paintTile(Graphics g, int x, int y, Image image)
		{
			g.drawImage(image, x * tileWidth, y * tileHeight, tileWidth, tileHeight, null);
		}
		
		public void setCharBuffer(Map<Coordinate,ColoredChar> charbuffer)
		{
			synchronized(this.charbuffer) {
				this.charbuffer.clear();
				this.charbuffer.putAll(charbuffer);
			}
		}
		
		public void setBackgroundBuffer(Map<Coordinate, Color> backgroundbuffer)
		{
			synchronized(this.backgroundbuffer) {
				this.backgroundbuffer.clear();
				this.backgroundbuffer.putAll(backgroundbuffer);
			}
		}
		
		public void setTileBuffer(Map<Coordinate, ColoredChar> tilebuffer)
		{
			synchronized(this.tilebuffer){
				this.tilebuffer.clear();
				this.tilebuffer.putAll(tilebuffer);
			}
		}
		
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			synchronized(backgroundbuffer){
				for (Coordinate coord : backgroundbuffer.keySet())
				{
					Color c = backgroundbuffer.get(coord);
					g.setColor(c);
					g.fillRect(coord.x()*tileWidth, coord.y()*tileHeight+2, tileWidth, tileHeight);
				}
			}
			synchronized(charbuffer){
				for (Coordinate coord : charbuffer.keySet())
				{
					ColoredChar ch = charbuffer.get(coord);
					paintChar(g, coord.x(), coord.y(), ch);
				}
			}
			synchronized(tilebuffer){
				for (Coordinate coord : tilebuffer.keySet())
				{
					ColoredChar ch = tilebuffer.get(coord);
					Image image = tileRegister.get(ch);
					if (image != null)
						paintTile(g, coord.x(), coord.y(), image);
					else
						paintChar(g, coord.x(), coord.y(), ch);
				}
			}
		}
	}

	private LegacyView (String title, int columns, int rows, int tileWidth, int tileHeight)
	{
		this.center_x = 0;
		this.center_y = 0;
		this.columns = columns;
		this.rows = rows;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.closeRequested = false;
		this.charbuffer = new HashMap<Coordinate, ColoredChar>();
		this.tilebuffer = new HashMap<Coordinate, ColoredChar>();
		this.backgroundbuffer = new HashMap<Coordinate, Color>();
	
		this.frame = new JFrame (title);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLocation(0,0);
		this.screen = new Screen(this.tileWidth, this.tileHeight);
		this.screen.setPreferredSize(new Dimension(this.columns * this.tileWidth, this.rows * this.tileHeight));
		this.screen.setBackground(Color.black);
		this.screen.setForeground(Color.white);
			
		this.frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing (WindowEvent e) {
				closeRequested = true;
			}
		});
			
		this.frame.add(this.screen, BorderLayout.CENTER);
		this.frame.pack();
			
		try {
			this.font = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("res/DejaVuSansMono.ttf"))
					.deriveFont(Font.PLAIN, tileHeight);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		this.screen.setFont(font);
		
		this.frame.setVisible(true);
	}
	
	public static View create (String title)
	{
		return create (title, 90, 27, 10, 16);
	}
	
	public static View create (String title, int columns, int rows, int tileWidth, int tileHeight)
	{
		return new LegacyView (title, columns, rows, tileWidth, tileHeight);
	}

	
	@Override
	public int tileHeight() {
		return tileHeight;
	}

	@Override
	public int tileWidth() {
		return tileWidth;
	}

	@Override
	public JFrame getFrame() {
		return frame;
	}

	@Override
	public void setCenter(Coordinate center) {
		setCenter(center.x(), center.y());
	}

	@Override
	public void setCenter(int x, int y) {
		center_x = x;
		center_y = y;
	}

	private void loadTile (String filename) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceAsStream(filename)));
		String ch = reader.readLine();
		String color = reader.readLine();
		reader.close();
		
		Guard.argumentsAreNotNull(ch, color);

		String pngfilename = filename.substring(0, filename.length()-4) + ".png";

		screen.registerTile(ColoredChar.create(ch.charAt(0), Color.decode("0x"+color)), pngfilename);

	}

	
	@Override
	public void loadTiles() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceAsStream("res/tiles/list")));

		String str;
		try {
			while ((str = reader.readLine()) != null)
			{
				loadTile (str);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public int columns() {
		return columns;
	}

	@Override
	public int rows() {
		return rows;
	}

	@Override
	public void clearTiles() {
		screen.clearTiles();
	}
	
	private int currentkey;

	static Map<Integer, Integer> keytranslatemap = createKeyTranslateMap();
	
	private static Map<Integer, Integer> createKeyTranslateMap()
	{
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		map.put(KeyEvent.VK_RIGHT, Keyboard.KEY_RIGHT);
		map.put(KeyEvent.VK_LEFT, Keyboard.KEY_LEFT);
		map.put(KeyEvent.VK_UP, Keyboard.KEY_UP);
		map.put(KeyEvent.VK_DOWN, Keyboard.KEY_DOWN);
		return map;
	}
	
	private boolean gotkey = false;

	@Override
	public boolean nextKey() {
		if (gotkey)
		{
			gotkey = false;
			return false;
		}
		try {
			Integer key = keytranslatemap.get(screen.consumeKey());
			if (key == null)
				currentkey = -1;
			else
				currentkey = key;
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public int getKeyEvent() {
		gotkey = true;
		return currentkey;
	}

	@Override
	public void clear() {
		charbuffer.clear();
		backgroundbuffer.clear();
		tilebuffer.clear();
	}

	@Override
	public void displayScreen(AsciiMap map) {
		try {
			clear();
			map.render(this, new Coordinate (0,0));
			update();
			while (this.screen.consumeKey () != ' ')
			{
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean closeRequested() {
		return closeRequested;
	}

	@Override
	public void drawBackground(Coordinate coord, Color background) {
		backgroundbuffer.put(coord, background);
	}

	@Override
	public void drawBackground(int x, int y, Color background) {
		drawBackground (new Coordinate (x, y), background);
	}

	@Override
	public void drawTile(Coordinate coord, ColoredChar ch) {
		tilebuffer.put(coord,  ch);
	}

	@Override
	public void drawTile(float x, float y, float z, ColoredChar ch) {
		drawTile (new Coordinate ((int)x, (int)y), ch);
	}

	@Override
	public void drawChar(Coordinate coord, ColoredChar ch) {
		charbuffer.put(coord, ch);
	}

	@Override
	public void drawChar(float x, float y, float z, ColoredChar ch) {
		drawChar(new Coordinate((int)x, (int)y), ch);
	}

	@Override
	public void drawString(float x, float y, float z, String string, Color color) {
		for (int i = 0; i < string.length(); i++)
		{
			drawChar (x + i, y, z, ColoredChar.create(string.charAt(i), color));
		}

	}

	@Override
	public void update() {
		screen.setCharBuffer(charbuffer);
		screen.setBackgroundBuffer(backgroundbuffer);
		screen.setTileBuffer(tilebuffer);
		screen.repaint();
	}

	@Override
	public boolean wasResized() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void drawWorld(World world) {
		Player player = world.getPlayer();
		clear();

        int viewborder_x = columns / 4;
        int viewborder_y = rows / 4;
        
        if (center_x - columns/2 + viewborder_x > player.pos().x())
        	center_x = player.pos().x() + columns/2 - viewborder_x;
        if (center_x + columns/2 - viewborder_x < player.pos().x())
        	center_x = player.pos().x() - columns/2 + viewborder_x;
        if (center_y - rows/2 + viewborder_y > player.pos().y())
        	center_y = player.pos().y() + rows/2 - viewborder_y;
        if (center_y + rows/2 - viewborder_y < player.pos().y())
        	center_y = player.pos().y() - rows/2 + viewborder_y;
        
        Collection<Coordinate> viewfield = player.getViewField ();
        
        for(int x = 0; x < columns; x++)
        {
            for(int y = 0; y < rows; y++)
            {
            	int worldx = center_x - columns/2 + x;
            	int worldy = center_y - rows/2 + y;
            	if (worldx < 0 || worldy < 0 || worldx >= world.width()
            		|| worldy >= world.height())
            		continue;
            	
            	Color background = world.lookBackground(worldx, worldy);

            	boolean insideviewfield = true;
            	
            	if (world.useViewfield() && !viewfield.contains(new Coordinate (worldx, worldy)))
            		insideviewfield = false;
            	
            	if (insideviewfield)
            	{
            		if (background == Color.black)
            			drawBackground (x, y, world.useViewfield()?Color.darkGray:Color.black);
            		else
            			drawBackground (x, y, background);
            		drawChar (x, y, 0, world.tileAt(worldx, worldy));

            		List<ColoredChar> list = world.lookAll(worldx, worldy);
                	Collections.reverse(list);
                	int i = 0;
                	int numchars = list.size()+1;
                	for (ColoredChar c : list)
                	{
                		if (c == null)
                			continue;
                		drawTile (x, y, ((float)i)/((float)numchars), c);
                		i++;
                	}
            	}
            	else if (world.isAlwaysVisible(worldx, worldy))
            	{
            		drawBackground (x, y, background.darker());
            		ColoredChar c = world.tileAt(worldx, worldy);
            		drawTile (x, y, 0, ColoredChar.create(c.ch(), c.color().darker()));
            	}
            }
        }
	}

}
