package pazi.ui;


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
import java.io.UnsupportedEncodingException;
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

import pazi.core.World;
import pazi.creature.Player;
import pazi.gen.map.AsciiMap;
import pazi.util.Guard;
import pazi.util.datatype.ColoredChar;
import pazi.util.datatype.Coordinate;


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
		this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.frame.setLocation(0,0);
		this.frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeRequested = true;
			}
		});
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
		
		this.frame.setIconImage(getIcon());
			
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
		BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceAsStream(filename), "UTF-8"));
		String ch = reader.readLine();
		String color = reader.readLine();
		reader.close();
		
		Guard.argumentsAreNotNull(ch, color);

		String pngfilename = filename.substring(0, filename.length()-4) + ".png";

		screen.registerTile(ColoredChar.create(ch.charAt(0), Color.decode("0x"+color)), pngfilename);

	}

	
	@Override
	public void loadTiles() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceAsStream("res/tiles/list"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

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

// TODO: Entsprechungen für die auskommentierten KeyEvents finden.
		map.put(KeyEvent.VK_0, Keyboard.KEY_0);
		map.put(KeyEvent.VK_1, Keyboard.KEY_1);
		map.put(KeyEvent.VK_2, Keyboard.KEY_2);
		map.put(KeyEvent.VK_3, Keyboard.KEY_3);
		map.put(KeyEvent.VK_4, Keyboard.KEY_4);
		map.put(KeyEvent.VK_5, Keyboard.KEY_5);
		map.put(KeyEvent.VK_6, Keyboard.KEY_6);
		map.put(KeyEvent.VK_7, Keyboard.KEY_7);
		map.put(KeyEvent.VK_8, Keyboard.KEY_8);
		map.put(KeyEvent.VK_9, Keyboard.KEY_9);
		map.put(KeyEvent.VK_A, Keyboard.KEY_A);
//		map.put(KeyEvent.VK_ACCEPT, Keyboard.KEY_ACCEPT);
		map.put(KeyEvent.VK_ADD, Keyboard.KEY_ADD);
//		map.put(KeyEvent.VK_AGAIN, Keyboard.KEY_AGAIN);
//		map.put(KeyEvent.VK_ALL_CANDIDATES, Keyboard.KEY_ALL_CANDIDATES);
//		map.put(KeyEvent.VK_ALPHANUMERIC, Keyboard.KEY_ALPHANUMERIC);
//		map.put(KeyEvent.VK_ALT, Keyboard.KEY_ALT);
//		map.put(KeyEvent.VK_ALT_GRAPH, Keyboard.KEY_ALT_GRAPH);
//		map.put(KeyEvent.VK_AMPERSAND, Keyboard.KEY_AMPERSAND);
//		map.put(KeyEvent.VK_ASTERISK, Keyboard.KEY_ASTERISK);
		map.put(KeyEvent.VK_AT, Keyboard.KEY_AT);
		map.put(KeyEvent.VK_B, Keyboard.KEY_B);
//		map.put(KeyEvent.VK_BACK_QUOTE, Keyboard.KEY_BACK_QUOTE);
//		map.put(KeyEvent.VK_BACK_SLASH, Keyboard.KEY_BACK_SLASH);
//		map.put(KeyEvent.VK_BACK_SPACE, Keyboard.KEY_BACK_SPACE);
//		map.put(KeyEvent.VK_BRACELEFT, Keyboard.KEY_BRACELEFT);
//		map.put(KeyEvent.VK_BRACERIGHT, Keyboard.KEY_BRACERIGHT);
		map.put(KeyEvent.VK_C, Keyboard.KEY_C);
//		map.put(KeyEvent.VK_CANCEL, Keyboard.KEY_CANCEL);
//		map.put(KeyEvent.VK_CAPS_LOCK, Keyboard.KEY_CAPS_LOCK);
		map.put(KeyEvent.VK_CIRCUMFLEX, Keyboard.KEY_CIRCUMFLEX);
//		map.put(KeyEvent.VK_CLEAR, Keyboard.KEY_CLEAR);
//		map.put(KeyEvent.VK_CLOSE_BRACKET, Keyboard.KEY_CLOSE_BRACKET);
//		map.put(KeyEvent.VK_CODE_INPUT, Keyboard.KEY_CODE_INPUT);
		map.put(KeyEvent.VK_COLON, Keyboard.KEY_COLON);
		map.put(KeyEvent.VK_COMMA, Keyboard.KEY_COMMA);
//		map.put(KeyEvent.VK_COMPOSE, Keyboard.KEY_COMPOSE);
//		map.put(KeyEvent.VK_CONTROL, Keyboard.KEY_CONTROL);
		map.put(KeyEvent.VK_CONVERT, Keyboard.KEY_CONVERT);
//		map.put(KeyEvent.VK_COPY, Keyboard.KEY_COPY);
//		map.put(KeyEvent.VK_CUT, Keyboard.KEY_CUT);
		map.put(KeyEvent.VK_D, Keyboard.KEY_D);
//		map.put(KeyEvent.VK_DEAD_ABOVEDOT, Keyboard.KEY_DEAD_ABOVEDOT);
//		map.put(KeyEvent.VK_DEAD_ABOVERING, Keyboard.KEY_DEAD_ABOVERING);
//		map.put(KeyEvent.VK_DEAD_ACUTE, Keyboard.KEY_DEAD_ACUTE);
//		map.put(KeyEvent.VK_DEAD_BREVE, Keyboard.KEY_DEAD_BREVE);
//		map.put(KeyEvent.VK_DEAD_CARON, Keyboard.KEY_DEAD_CARON);
//		map.put(KeyEvent.VK_DEAD_CEDILLA, Keyboard.KEY_DEAD_CEDILLA);
//		map.put(KeyEvent.VK_DEAD_CIRCUMFLEX, Keyboard.KEY_DEAD_CIRCUMFLEX);
//		map.put(KeyEvent.VK_DEAD_DIAERESIS, Keyboard.KEY_DEAD_DIAERESIS);
//		map.put(KeyEvent.VK_DEAD_DOUBLEACUTE, Keyboard.KEY_DEAD_DOUBLEACUTE);
//		map.put(KeyEvent.VK_DEAD_GRAVE, Keyboard.KEY_DEAD_GRAVE);
//		map.put(KeyEvent.VK_DEAD_IOTA, Keyboard.KEY_DEAD_IOTA);
//		map.put(KeyEvent.VK_DEAD_MACRON, Keyboard.KEY_DEAD_MACRON);
//		map.put(KeyEvent.VK_DEAD_OGONEK, Keyboard.KEY_DEAD_OGONEK);
//		map.put(KeyEvent.VK_DEAD_SEMIVOICED_SOUND, Keyboard.KEY_DEAD_SEMIVOICED_SOUND);
//		map.put(KeyEvent.VK_DEAD_TILDE, Keyboard.KEY_DEAD_TILDE);
//		map.put(KeyEvent.VK_DEAD_VOICED_SOUND, Keyboard.KEY_DEAD_VOICED_SOUND);
		map.put(KeyEvent.VK_DECIMAL, Keyboard.KEY_DECIMAL);
		map.put(KeyEvent.VK_DELETE, Keyboard.KEY_DELETE);
		map.put(KeyEvent.VK_DIVIDE, Keyboard.KEY_DIVIDE);
//		map.put(KeyEvent.VK_DOLLAR, Keyboard.KEY_DOLLAR);
		map.put(KeyEvent.VK_DOWN, Keyboard.KEY_DOWN);
		map.put(KeyEvent.VK_E, Keyboard.KEY_E);
		map.put(KeyEvent.VK_END, Keyboard.KEY_END);
		map.put(KeyEvent.VK_ENTER, Keyboard.KEY_RETURN);
		map.put(KeyEvent.VK_EQUALS, Keyboard.KEY_EQUALS);
		map.put(KeyEvent.VK_ESCAPE, Keyboard.KEY_ESCAPE);
//		map.put(KeyEvent.VK_EURO_SIGN, Keyboard.KEY_EURO_SIGN);
//		map.put(KeyEvent.VK_EXCLAMATION_MARK, Keyboard.KEY_EXCLAMATION_MARK);
		map.put(KeyEvent.VK_F, Keyboard.KEY_F);
		map.put(KeyEvent.VK_F1, Keyboard.KEY_F1);
		map.put(KeyEvent.VK_F10, Keyboard.KEY_F10);
		map.put(KeyEvent.VK_F11, Keyboard.KEY_F11);
		map.put(KeyEvent.VK_F12, Keyboard.KEY_F12);
		map.put(KeyEvent.VK_F13, Keyboard.KEY_F13);
		map.put(KeyEvent.VK_F14, Keyboard.KEY_F14);
		map.put(KeyEvent.VK_F15, Keyboard.KEY_F15);
//		map.put(KeyEvent.VK_F16, Keyboard.KEY_F16);
//		map.put(KeyEvent.VK_F17, Keyboard.KEY_F17);
//		map.put(KeyEvent.VK_F18, Keyboard.KEY_F18);
//		map.put(KeyEvent.VK_F19, Keyboard.KEY_F19);
		map.put(KeyEvent.VK_F2, Keyboard.KEY_F2);
//		map.put(KeyEvent.VK_F20, Keyboard.KEY_F20);
//		map.put(KeyEvent.VK_F21, Keyboard.KEY_F21);
//		map.put(KeyEvent.VK_F22, Keyboard.KEY_F22);
//		map.put(KeyEvent.VK_F23, Keyboard.KEY_F23);
//		map.put(KeyEvent.VK_F24, Keyboard.KEY_F24);
		map.put(KeyEvent.VK_F3, Keyboard.KEY_F3);
		map.put(KeyEvent.VK_F4, Keyboard.KEY_F4);
		map.put(KeyEvent.VK_F5, Keyboard.KEY_F5);
		map.put(KeyEvent.VK_F6, Keyboard.KEY_F6);
		map.put(KeyEvent.VK_F7, Keyboard.KEY_F7);
		map.put(KeyEvent.VK_F8, Keyboard.KEY_F8);
		map.put(KeyEvent.VK_F9, Keyboard.KEY_F9);
//		map.put(KeyEvent.VK_FINAL, Keyboard.KEY_FINAL);
//		map.put(KeyEvent.VK_FIND, Keyboard.KEY_FIND);
//		map.put(KeyEvent.VK_FULL_WIDTH, Keyboard.KEY_FULL_WIDTH);
		map.put(KeyEvent.VK_G, Keyboard.KEY_G);
//		map.put(KeyEvent.VK_GREATER, Keyboard.KEY_GREATER);
		map.put(KeyEvent.VK_H, Keyboard.KEY_H);
//		map.put(KeyEvent.VK_HALF_WIDTH, Keyboard.KEY_HALF_WIDTH);
//		map.put(KeyEvent.VK_HELP, Keyboard.KEY_HELP);
//		map.put(KeyEvent.VK_HIRAGANA, Keyboard.KEY_HIRAGANA);
		map.put(KeyEvent.VK_HOME, Keyboard.KEY_HOME);
		map.put(KeyEvent.VK_I, Keyboard.KEY_I);
//		map.put(KeyEvent.VK_INPUT_METHOD_ON_OFF, Keyboard.KEY_INPUT_METHOD_ON_OFF);
		map.put(KeyEvent.VK_INSERT, Keyboard.KEY_INSERT);
//		map.put(KeyEvent.VK_INVERTED_EXCLAMATION_MARK, Keyboard.KEY_INVERTED_EXCLAMATION_MARK);
		map.put(KeyEvent.VK_J, Keyboard.KEY_J);
//		map.put(KeyEvent.VK_JAPANESE_HIRAGANA, Keyboard.KEY_JAPANESE_HIRAGANA);
//		map.put(KeyEvent.VK_JAPANESE_KATAKANA, Keyboard.KEY_JAPANESE_KATAKANA);
//		map.put(KeyEvent.VK_JAPANESE_ROMAN, Keyboard.KEY_JAPANESE_ROMAN);
		map.put(KeyEvent.VK_K, Keyboard.KEY_K);
		map.put(KeyEvent.VK_KANA, Keyboard.KEY_KANA);
//		map.put(KeyEvent.VK_KANA_LOCK, Keyboard.KEY_KANA_LOCK);
		map.put(KeyEvent.VK_KANJI, Keyboard.KEY_KANJI);
//		map.put(KeyEvent.VK_KATAKANA, Keyboard.KEY_KATAKANA);
//		map.put(KeyEvent.VK_KP_DOWN, Keyboard.KEY_KP_DOWN);
//		map.put(KeyEvent.VK_KP_LEFT, Keyboard.KEY_KP_LEFT);
//		map.put(KeyEvent.VK_KP_RIGHT, Keyboard.KEY_KP_RIGHT);
//		map.put(KeyEvent.VK_KP_UP, Keyboard.KEY_KP_UP);
		map.put(KeyEvent.VK_L, Keyboard.KEY_L);
		map.put(KeyEvent.VK_LEFT, Keyboard.KEY_LEFT);
//		map.put(KeyEvent.VK_LEFT_PARENTHESIS, Keyboard.KEY_LEFT_PARENTHESIS);
//		map.put(KeyEvent.VK_LESS, Keyboard.KEY_LESS);
		map.put(KeyEvent.VK_M, Keyboard.KEY_M);
//		map.put(KeyEvent.VK_META, Keyboard.KEY_META);
		map.put(KeyEvent.VK_MINUS, Keyboard.KEY_MINUS);
//		map.put(KeyEvent.VK_MODECHANGE, Keyboard.KEY_MODECHANGE);
		map.put(KeyEvent.VK_MULTIPLY, Keyboard.KEY_MULTIPLY);
		map.put(KeyEvent.VK_N, Keyboard.KEY_N);
//		map.put(KeyEvent.VK_NONCONVERT, Keyboard.KEY_NONCONVERT);
//		map.put(KeyEvent.VK_NUM_LOCK, Keyboard.KEY_NUM_LOCK);
//		map.put(KeyEvent.VK_NUMBER_SIGN, Keyboard.KEY_NUMBER_SIGN);
		map.put(KeyEvent.VK_NUMPAD0, Keyboard.KEY_NUMPAD0);
		map.put(KeyEvent.VK_NUMPAD1, Keyboard.KEY_NUMPAD1);
		map.put(KeyEvent.VK_NUMPAD2, Keyboard.KEY_NUMPAD2);
		map.put(KeyEvent.VK_NUMPAD3, Keyboard.KEY_NUMPAD3);
		map.put(KeyEvent.VK_NUMPAD4, Keyboard.KEY_NUMPAD4);
		map.put(KeyEvent.VK_NUMPAD5, Keyboard.KEY_NUMPAD5);
		map.put(KeyEvent.VK_NUMPAD6, Keyboard.KEY_NUMPAD6);
		map.put(KeyEvent.VK_NUMPAD7, Keyboard.KEY_NUMPAD7);
		map.put(KeyEvent.VK_NUMPAD8, Keyboard.KEY_NUMPAD8);
		map.put(KeyEvent.VK_NUMPAD9, Keyboard.KEY_NUMPAD9);
		map.put(KeyEvent.VK_O, Keyboard.KEY_O);
//		map.put(KeyEvent.VK_OPEN_BRACKET, Keyboard.KEY_OPEN_BRACKET);
		map.put(KeyEvent.VK_P, Keyboard.KEY_P);
//		map.put(KeyEvent.VK_PAGE_DOWN, Keyboard.KEY_PAGE_DOWN);
//		map.put(KeyEvent.VK_PAGE_UP, Keyboard.KEY_PAGE_UP);
//		map.put(KeyEvent.VK_PASTE, Keyboard.KEY_PASTE);
		map.put(KeyEvent.VK_PAUSE, Keyboard.KEY_PAUSE);
		map.put(KeyEvent.VK_PERIOD, Keyboard.KEY_PERIOD);
//		map.put(KeyEvent.VK_PLUS, Keyboard.KEY_PLUS);
//		map.put(KeyEvent.VK_PREVIOUS_CANDIDATE, Keyboard.KEY_PREVIOUS_CANDIDATE);
//		map.put(KeyEvent.VK_PRINTSCREEN, Keyboard.KEY_PRINTSCREEN);
//		map.put(KeyEvent.VK_PROPS, Keyboard.KEY_PROPS);
		map.put(KeyEvent.VK_Q, Keyboard.KEY_Q);
//		map.put(KeyEvent.VK_QUOTE, Keyboard.KEY_QUOTE);
//		map.put(KeyEvent.VK_QUOTEDBL, Keyboard.KEY_QUOTEDBL);
		map.put(KeyEvent.VK_R, Keyboard.KEY_R);
		map.put(KeyEvent.VK_RIGHT, Keyboard.KEY_RIGHT);
//		map.put(KeyEvent.VK_RIGHT_PARENTHESIS, Keyboard.KEY_RIGHT_PARENTHESIS);
//		map.put(KeyEvent.VK_ROMAN_CHARACTERS, Keyboard.KEY_ROMAN_CHARACTERS);
		map.put(KeyEvent.VK_S, Keyboard.KEY_S);
//		map.put(KeyEvent.VK_SCROLL_LOCK, Keyboard.KEY_SCROLL_LOCK);
		map.put(KeyEvent.VK_SEMICOLON, Keyboard.KEY_SEMICOLON);
//		map.put(KeyEvent.VK_SEPARATER, Keyboard.KEY_SEPARATER);
//		map.put(KeyEvent.VK_SEPARATOR, Keyboard.KEY_SEPARATOR);
//		map.put(KeyEvent.VK_SHIFT, Keyboard.KEY_SHIFT);
		map.put(KeyEvent.VK_SLASH, Keyboard.KEY_SLASH);
		map.put(KeyEvent.VK_SPACE, Keyboard.KEY_SPACE);
		map.put(KeyEvent.VK_STOP, Keyboard.KEY_STOP);
		map.put(KeyEvent.VK_SUBTRACT, Keyboard.KEY_SUBTRACT);
		map.put(KeyEvent.VK_T, Keyboard.KEY_T);
		map.put(KeyEvent.VK_TAB, Keyboard.KEY_TAB);
		map.put(KeyEvent.VK_U, Keyboard.KEY_U);
//		map.put(KeyEvent.VK_UNDEFINED, Keyboard.KEY_UNDEFINED);
//		map.put(KeyEvent.VK_UNDERSCORE, Keyboard.KEY_UNDERSCORE);
//		map.put(KeyEvent.VK_UNDO, Keyboard.KEY_UNDO);
		map.put(KeyEvent.VK_UP, Keyboard.KEY_UP);
		map.put(KeyEvent.VK_V, Keyboard.KEY_V);
		map.put(KeyEvent.VK_W, Keyboard.KEY_W);
		map.put(KeyEvent.VK_X, Keyboard.KEY_X);
		map.put(KeyEvent.VK_Y, Keyboard.KEY_Y);
		map.put(KeyEvent.VK_Z, Keyboard.KEY_Z);
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
	
	public void resetCloseRequested() {
		closeRequested = false;
	}

	@Override
	public void drawBackground(Coordinate coord, Color background) {
		charbuffer.remove(coord);
		tilebuffer.remove(coord);
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
	
	public void cleanup()
	{
		
	}

}
