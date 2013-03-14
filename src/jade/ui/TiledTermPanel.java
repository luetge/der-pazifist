package jade.ui;

import jade.core.World;
import jade.util.Guard;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class TiledTermPanel extends TermPanel
{
    public static final int DEFAULT_TILESIZE = 16;

    private Map<Coordinate, List<ColoredChar>> tileBuffer;
    private Map<Coordinate, List<ColoredChar>> savedTile;

    public TiledTermPanel(int columns, int rows, int tile_size)
    {
        this(new TiledScreen(columns, rows, tile_size));
    }

    public TiledTermPanel()
    {
        this(new TiledScreen(DEFAULT_COLS, DEFAULT_ROWS, DEFAULT_TILESIZE));
    }

    private TiledTermPanel(TiledScreen screen)
    {
        super(screen);
        tileBuffer = new HashMap<Coordinate, List<ColoredChar>>();
        savedTile = new HashMap<Coordinate, List<ColoredChar>>();
    }

    public static TiledTermPanel getFramedTerminal(String title)
    {
        TiledTermPanel term = new TiledTermPanel();
        frameTermPanel(term, title);
        return term;
    }

    @Override
    protected TiledScreen screen()
    {
        return (TiledScreen) super.screen();
    }

    public void bufferTile(Coordinate coord, ColoredChar ch)
    {
        Guard.argumentsAreNotNull(coord, ch);
        synchronized(tileBuffer) {
        	List<ColoredChar> list = tileBuffer.get(coord);
        	if (list == null)
        	{
        		list = new ArrayList<ColoredChar> ();
        		tileBuffer.put(coord, list);
        	}
        	list.add (ch);
        }
    }
    
    public void loadTiles (String dirname)
    {
		File tiledir = new File(dirname);
		Guard.verifyState(tiledir.isDirectory());
		File files[] = tiledir.listFiles(new FilenameFilter() {
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
				
				registerTile (filename, 0, 0, ColoredChar.create(ch.charAt(0), Color.decode("0x"+color)));
				
			} catch(FileNotFoundException e) {
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		}

    }
    
    public final void bufferTile (int x, int y, ColoredChar ch)
    {
    	bufferTile (new Coordinate (x, y), ch);
    }


    public boolean registerTile(String tileSet, int x, int y, ColoredChar ch)
    {
        try
        {
            int w = screen().tileWidth();
            int h = screen().tileHeight();
            BufferedImage tileset = ImageIO.read(new File(tileSet));
            BufferedImage tile = tileset.getSubimage(x * w, y * h, w, h);
            screen().registerTile(ch, tile);
            return true;
        }
        catch(IOException e)
        {
            return false;
        }
    }

    @Override
    public void clearBuffer()
    {
        super.clearBuffer();
        tileBuffer.clear();
    }

    @Override
    public void saveBuffer()
    {
        super.saveBuffer();
        savedTile.clear();
        savedTile.putAll(tileBuffer);
    }

    @Override
    public void recallBuffer()
    {
        super.recallBuffer();
        tileBuffer.clear();
        tileBuffer.putAll(savedTile);
    }

    @Override
    public void refreshScreen()
    {
        screen().setTileBuffer(tileBuffer);
        super.refreshScreen();
    }

    @Override
    public void bufferCamera(Camera camera)
    {
        Guard.argumentIsNotNull(camera);
        Guard.verifyState(cameraRegistered(camera));

        Coordinate screenCenter = cameraCenter(camera);
        int offX = screenCenter.x() - camera.x();
        int offY = screenCenter.y() - camera.y();
        World world = camera.world();
        for(Coordinate coord : camera.getViewField())
            tileBuffer.put(coord.getTranslated(offX, offY),
                    world.lookAll(coord));
    }
    
    @Override
    public void unbuffer (Coordinate coord)
    {
    	super.unbuffer(coord);
    	tileBuffer.remove(coord);
    }

    @Override
    public void bufferRelative(Camera camera, ColoredChar ch, int x, int y)
    {
        Guard.argumentIsNotNull(camera);
        Guard.verifyState(cameraRegistered(camera));

        Coordinate screenCenter = cameraCenter(camera);
        int offX = screenCenter.x() - camera.x();
        int offY = screenCenter.y() - camera.y();
        Coordinate pos = new Coordinate(x + offX, y + offY);
        List<ColoredChar> look = tileBuffer.containsKey(pos) ? tileBuffer
                .get(pos) : new ArrayList<ColoredChar>();
        look.add(0, ch);
        tileBuffer.put(new Coordinate(x + offX, y + offY), look);
    }

    private static class TiledScreen extends Screen
    {
        private static final long serialVersionUID = 6739172935885377439L;

        private Map<Coordinate, List<ColoredChar>> tileBuffer;
        private Map<ColoredChar, Image> tileRegister;

        public TiledScreen(int columns, int rows, int tileSize)
        {
            this(columns, rows, 5*tileSize/8, tileSize);
        }

        public TiledScreen(int columns, int rows, int tileWidth, int tileHeight)
        {
            super(columns, rows, tileWidth, tileHeight);
            tileBuffer = new HashMap<Coordinate, List<ColoredChar>>();
            tileRegister = new HashMap<ColoredChar, Image>();
        }

        public void setTileBuffer(Map<Coordinate, List<ColoredChar>> buffer)
        {
            synchronized(tileBuffer)
            {
                tileBuffer.clear();
                tileBuffer.putAll(buffer);
            }
        }

        public void registerTile(ColoredChar ch, Image tile)
        {
            synchronized(tileRegister)
            {
                tileRegister.put(ch, tile);
            }
        }

        @Override
        protected void paintComponent(Graphics page)
        {
            super.paintComponent(page);
            synchronized(tileBuffer)
            {
            	for(Coordinate coord : tileBuffer.keySet())
            	{
            		int x = tileWidth() * coord.x();
            		int y = tileHeight() * coord.y();
                
            		List<ColoredChar> tiles = tileBuffer.get(coord);
            		for(ColoredChar ch : tiles)
            		{
            			if(tileRegister.containsKey(ch))
            			{
            				page.drawImage(tileRegister.get(ch), x, y,
                                tileWidth(), tileHeight(), null);
            			}
            			else
            			{
            				paintChar (page, x, y + tileHeight(), ch);
            			}
            		}
                
            	}
            }
        }
    }
}
