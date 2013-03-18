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
import java.awt.event.KeyEvent;
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


public class View {
	private int center_x, center_y;
	
	private int columns;
	private int rows;
	private int tileWidth;
	private int tileHeight;
	private Font font;
	private boolean closeRequested;
	
	private JFrame frame;
	private Canvas canvas;
	
	static int nextpowerof2 (int n)
	{
		Guard.argumentIsPositive(n);
		int i = 1;
		while (i < n)
			i <<= 1;
		return i;
	}
	
	public JFrame getFrame ()
	{
		return frame;
	}
	
	private class GLTile
	{
		private float tw, th;
		private int tex;
		public GLTile(String filename)
		{
			try {
				Texture texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(filename));
				tex = texture.getTextureID();

				tw = ((float)texture.getImageWidth())/((float)texture.getTextureWidth());
				th = ((float)texture.getImageHeight())/((float)texture.getTextureHeight());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		public void draw (float x, float y, float z)
		{
			GL11.glColor3f(1, 1, 1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D,  tex);
    		GL11.glBegin(GL11.GL_QUADS);
    		GL11.glTexCoord2f(0, 0);
    		GL11.glVertex3f(x,y,z);
    		GL11.glTexCoord2f(tw, 0);
    		GL11.glVertex3f(x+1,y,z);
    		GL11.glTexCoord2f(tw, th);
    		GL11.glVertex3f(x+1,y+1,z);
    		GL11.glTexCoord2f(0,th);
    		GL11.glVertex3f(x,y+1,z);
    		GL11.glEnd();
    	}
	}

	private class GLChar
	{
		private float tw, th;
		private float w, h;
		private int tex;
		public GLChar (Character ch)
		{
			FontMetrics fm = canvas.getGraphics().getFontMetrics(font);
			int charwidth = fm.charWidth(ch);
			int charwidth_p2 = nextpowerof2(charwidth*2);
			int charheight = fm.getHeight();
			int charheight_p2 = nextpowerof2(charheight*2);
			
			WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,charwidth_p2,charheight_p2,4,null);
			
			ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
					new int[] {8,8,8,8}, true, false, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
			BufferedImage image = new BufferedImage(cm, raster, false, null);
			
			Graphics g = image.getGraphics();
	        g.setFont(font);
	        fm = g.getFontMetrics();

	        g.setColor(Color.white);
			g.drawString(ch.toString(), 0, fm.getAscent());
			
			byte data[] = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
			ByteBuffer imagebuffer = BufferUtils.createByteBuffer(data.length);
			imagebuffer.order(ByteOrder.nativeOrder());
			imagebuffer.put(data, 0, data.length);
			imagebuffer.flip();
			tex = GL11.glGenTextures();

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, charwidth_p2, charheight_p2, 0, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, imagebuffer);
			
			tw = ((float)charwidth) / ((float)charwidth_p2);
			th = ((float)charheight) / ((float)charheight_p2);
			
			w = ((float)charwidth)/((float)tileWidth);
			h = ((float)charheight)/((float)tileHeight);
		}
		
		public void draw (float x, float y, float z)
		{
			GL11.glBindTexture(GL11.GL_TEXTURE_2D,  tex);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(x,y,z);
			GL11.glTexCoord2f(tw, 0);
			GL11.glVertex3f(x+w,y,z);
			GL11.glTexCoord2f(tw, th);
			GL11.glVertex3f(x+w,y+h,z);
			GL11.glTexCoord2f(0, th);
			GL11.glVertex3f(x, y+h,z);
			GL11.glEnd();
		}
	}

	Map<ColoredChar, GLTile> gltiles;
	Map<Character, GLChar> glchars; 
	
	private final AtomicReference<Dimension> newCanvasSize = new AtomicReference<Dimension> ();
	
	private View (String title, int columns, int rows, int tileWidth, int tileHeight)
	{
		this.center_x = 0;
		this.center_y = 0;
		this.columns = columns;
		this.rows = rows;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.closeRequested = false;
		this.gltiles = new HashMap<ColoredChar, GLTile> ();
		this.glchars = new HashMap<Character, GLChar> ();
	
		try {
			
			this.frame = new JFrame (title);
			this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.frame.setLocation(0,0);
			this.canvas = new Canvas();
			this.canvas.setSize(this.columns * this.tileWidth, this.rows * this.tileHeight);
			
			this.canvas.addComponentListener(new ComponentAdapter(){
				@Override
				public void componentResized(ComponentEvent e) {
					newCanvasSize.set(canvas.getSize());
				}
			});
			this.frame.addWindowFocusListener(new WindowAdapter(){
				@Override
				public void windowGainedFocus (WindowEvent e) {
					canvas.requestFocusInWindow();
				}
			});
			this.frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing (WindowEvent e) {
					closeRequested = true;
				}
			});
			
			this.frame.add(this.canvas, BorderLayout.CENTER);
			this.frame.pack();
			
			try {
				this.font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream ("res/DejaVuSansMono.ttf"))
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
			
			this.canvas.setFont(font);
			
			Display.setDisplayMode(new DisplayMode (canvas.getWidth(), canvas.getHeight()));
			Display.setLocation(0, 0);
			Display.setTitle(title);
			Display.setVSyncEnabled(true);
			Display.setResizable(true);
			Display.setParent (this.canvas);
			Display.create();
			
			Keyboard.enableRepeatEvents(true);
			
			this.frame.setVisible(true);
			
			GL11.glClearColor(0, 0, 0, 1);

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, columns, rows, 0, -1, 1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			
    		GL11.glEnable(GL11.GL_TEXTURE_2D);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private static View view = null;
	
	public int tileHeight()
	{
		return tileHeight;
	}
	
	public int tileWidth()
	{
		return tileWidth;
	}
	
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
	}
	
	public void clear ()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}
	
	public void displayScreen (AsciiMap screen)
	{
		clear();
		screen.render(view, new Coordinate (0,0));
		while (Keyboard.getEventCharacter () != ' ')
		{
			Display.update();
			Keyboard.next();
		}
	}
	
	public boolean closeRequested ()
	{
		return Display.isCloseRequested() || closeRequested;
	}
	
	public void drawBackground (Coordinate coord, Color background)
	{
		drawBackground (coord.x(), coord.y(), background);
	}
	
	public void drawBackground (int x, int y, Color background)
	{
		if (background == Color.black)
			return;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4ub((byte)background.getRed(), (byte)background.getGreen(), (byte)background.getBlue(), (byte)background.getAlpha());
		float dy = 2.0f/tileWidth;
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3f(x, y+dy, -1);
		GL11.glVertex3f(x+1, y+dy, -1);
		GL11.glVertex3f(x+1, y+dy+1, -1);
		GL11.glVertex3f(x, y+dy+1, -1);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public void drawTile (Coordinate coord, ColoredChar ch)
	{
		drawTile (coord.x(), coord.y(), 0, ch);
	}
	
	public void drawTile (float x, float y, float z, ColoredChar ch)
	{
		if (ch.ch() == ' ')
			return;
		GLTile tile = gltiles.get(ch);
		if (tile != null)
		{
			tile.draw(x, y, z);
		}
		else
		{
			drawChar(x, y, z, ch);
		}
	}
	
	public void drawChar (Coordinate coord, ColoredChar ch)
	{
		drawChar (coord.x(), coord.y(), 0, ch);
	}
	
	public void drawChar (float x, float y, float z, ColoredChar ch)
	{
		if (ch.ch() == ' ')
			return;
		GLChar glchar = glchars.get(ch.ch());
		if (glchar == null)
		{
			glchar = new GLChar (ch.ch());
			glchars.put(ch.ch(), glchar);
		}
		
		GL11.glColor3ub((byte)ch.color().getRed(), (byte)ch.color().getGreen(), (byte)ch.color().getBlue());
		glchar.draw(x, y, z);
	}
	
	public void drawString (float x, float y, float z, String string, Color color)
	{
		for (int i = 0; i < string.length(); i++)
		{
			drawChar (x + i, y, z, ColoredChar.create(string.charAt(i), color));
		}
	}
	
	public void update ()
	{
        Display.update();
	}
	
	public boolean wasResized ()
	{
		return Display.wasResized();
	}
	
	private void resizeView (int width, int height)
	{
		int viewport_width = width;
		int viewport_height = height;
		
		if ((float)width / (float) height > (float)(columns * tileWidth) / (float) (rows * tileHeight))
		{
			// Fenster zu breit
			viewport_width = (int)(height * (float)(columns * tileWidth) / (float) (rows * tileHeight));
		}
		if ((float)height / (float) width > (float)(rows * tileHeight) / (float) (columns * tileWidth))
		{
			// Fenster zu schmal
			viewport_height =  (int)(width * (float)(rows * tileHeight) / (float) (columns * tileWidth));
		}
		
		GL11.glViewport(0,  height - viewport_height, viewport_width, viewport_height);
	}

	
	public void drawWorld (World world)
	{
		Player player = world.getPlayer();
		resizeView(canvas.getWidth(), canvas.getHeight());
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
