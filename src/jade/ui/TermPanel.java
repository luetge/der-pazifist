package jade.ui;

import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Implements a {@code Terminal} on a {@code JPanel}, which can then be embedded into any container
 * able to use a {@code JPanel}.
 */
public class TermPanel extends Terminal
{
    public static final int DEFAULT_COLS = 90;
    public static final int DEFAULT_ROWS = 27;
    public static final int DEFAULT_SIZE = 12;
    
    private Screen screen;

    /**
     * Constructs a new {@code TermPanel} with the given dimensions. Note that the rows and columns
     * can be changed by resizing the underlying JPanel, but font size is fixed.
     * @param columns the default number of columns to display
     * @param rows the default number of rows to display
     * @param fontSize the size of each tile
     */
    public TermPanel(int columns, int rows, int fontSize)
    {
        this(new Screen(columns, rows, fontSize));
    }
    
    /**
     * Constructs a new {@code TermPanel} with the default dimensions. There will be 80 columns, 24
     * rows, and a font size of 12.
     */
    public TermPanel()
    {
        this(DEFAULT_COLS, DEFAULT_ROWS, DEFAULT_SIZE);
    }
    
    protected TermPanel(Screen screen)
    {
        this.screen = screen;
    }

    /**
     * Constructs and returns a new {@code TermPanel} with default dimensions, which is placed
     * inside a {@code JFrame}. The {@code TermPanel} will initially have 80 columns, 24 rows, and a
     * font size of 12.
     * @param title the title of the {@code JFrame}
     * @return a new {@code TermPanel} with default dimensions.
     */
    public static TermPanel getFramedTerminal(String title)
    {
        TermPanel term = new TermPanel();
        frameTermPanel(term, title);
        return term;
    }
    
    protected static void frameTermPanel(TermPanel term, String title)
    {
        JFrame frame = new JFrame(title);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
        frame.add(HUD.getPanel());
        frame.add(term.panel());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public void addComponentListener(ComponentListener l)
    {
    	screen ().addComponentListener(l);
    }

    /**
     * Returns the underlying {@code JPanel} display of the {@code TermPanel}. This {@code JPanel}
     * can then be embedded in any other container like a normal {@code JPanel}.
     * @return the underlying {@code JPanel} display of the {@code TermPanel}
     */
    public JPanel panel()
    {
        return screen;
    }
    
    public int tileHeight()
    {
    	return screen().tileHeight();
    }
    
    protected Screen screen()
    {
        return screen;
    }

    @Override
    public int getKey() throws InterruptedException
    {
        return screen.consumeKeyPress();
    }

    @Override
    public void refreshScreen()
    {
        screen.setBuffer(getBuffer());
        screen.setBackgroundBuffer(getBackgroundBuffer());
        screen.repaint();
    }
    
    public int width ()
    {
    	return screen.getWidth () / screen.tileWidth ();
    }
    public int height ()
    {
    	return screen.getHeight () / screen.tileHeight ();
    }
    
    public void setCurrentConsoleText(String sText){
    	screen.setCurrentConsoleText(sText);
    }

    protected static class Screen extends JPanel implements KeyListener
    {
        private static final long serialVersionUID = 7219226976524388778L;

        private int tileWidth;
        private int tileHeight;
        private BlockingQueue<Integer> inputBuffer;
        private Map<Coordinate, ColoredChar> screenBuffer;
        private Map<Coordinate, Color> backgroundBuffer;
        private String sCurrentConsoleText = "";

        public Screen(int columns, int rows, int fontSize)
        {
            this(columns, rows, fontSize * 3/4, fontSize);
        }
        
        public Screen(int columns, int rows, int tileWidth, int tileHeight)
        {
        	try {
            inputBuffer = new LinkedBlockingQueue<Integer>();
            screenBuffer = new HashMap<Coordinate, ColoredChar>();
            backgroundBuffer = new HashMap<Coordinate, Color>();
            addKeyListener(this);
            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
            setPreferredSize(new Dimension(columns * tileWidth, rows * tileHeight));
            Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream ("res/DejaVuSansMono.ttf"));
            setFont(font.deriveFont(Font.PLAIN, tileHeight));
            setBackground(Color.black);
            setFocusable(true);
    		// TODO: proper error handling
        	} catch(IOException e) {
        		e.printStackTrace();
        	} catch(FontFormatException e) {
        		e.printStackTrace();
        	}
        }
        
        public void setCurrentConsoleText(String sText){
        	this.sCurrentConsoleText = sText;
        }

        protected int tileWidth()
        {
            return tileWidth;
        }
        
        protected int tileHeight()
        {
            return tileHeight;
        }

        @Override
        protected void paintComponent(Graphics page)
        {
            super.paintComponent(page);
            synchronized(backgroundBuffer)
            {
                for(Coordinate coord : backgroundBuffer.keySet())
                {
                	Color c = backgroundBuffer.get(coord);
                	if (c == Color.black)
                		continue;
                    int x = tileWidth * coord.x();
                    int y = tileHeight * coord.y();
                	page.setColor(c);
                	page.fillRect(x, y+2,	tileWidth, tileHeight);
                }
            }
            synchronized(screenBuffer)
            {
                for(Coordinate coord : screenBuffer.keySet())
                {
                    ColoredChar ch = screenBuffer.get(coord);
                    if (ch.ch () == ' ')
                    	continue;
                    int x = tileWidth * coord.x();
                    int y = tileHeight * (coord.y() + 1);
                    
                    String str = ch.toString ();

                    page.setColor(ch.color());
                    page.drawString(str, x, y);
                }
                page.setColor(Color.white);
                page.drawString(sCurrentConsoleText, 0, tileHeight);
            }
        }

        public void setBuffer(Map<Coordinate, ColoredChar> buffer)
        {
            synchronized(screenBuffer)
            {
                screenBuffer.clear();
                screenBuffer.putAll(buffer);
            }
        }
        
        public void setBackgroundBuffer(Map<Coordinate, Color> buffer)
        {
        	synchronized(backgroundBuffer)
        	{
        		backgroundBuffer.clear();
        		backgroundBuffer.putAll(buffer);
        	}
        }

        @Override
        public void keyPressed(KeyEvent event)
        {
            inputBuffer.offer(event.getKeyCode());
        }

        public int consumeKeyPress() throws InterruptedException
        {
            return inputBuffer.take();
        }

        @Override
        public void keyReleased(KeyEvent e)
        {}

        @Override
        public void keyTyped(KeyEvent e)
        {}
    }
}
