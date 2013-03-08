package rogue;

import jade.core.World;
import jade.ui.TiledTermPanel;
import jade.util.datatype.ColoredChar;

import java.awt.Color;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;

import pazi.Display;
import rogue.creature.Monster;
import rogue.creature.Player;
import rogue.level.Level;

public class Rogue implements ComponentListener
{
	private TiledTermPanel term;
	private Player player;
	private World world;
	private int viewcenter_x;
	private int viewcenter_y;
	
	Rogue () throws InterruptedException
	{
		term = TiledTermPanel.getFramedTerminal("Der PaziFist");
        term.registerTile("dungeon.png", 5, 59, ColoredChar.create('#'));
        term.registerTile("dungeon.png", 3, 60, ColoredChar.create('.'));
        term.registerTile("dungeon.png", 5, 20, ColoredChar.create('@'));
        term.registerTile("dungeon.png", 14, 30, ColoredChar.create('D', Color.red));
        
        player = new Player(term);
        world = new Level(256, 196, player);
		viewcenter_x = player.pos().x();
		viewcenter_y = player.pos().y();
		for(int i=0;i<600; i++)
			world.addActor(new Monster(ColoredChar.create('Z', Color.green)));
        
        term.addComponentListener(this);
        
		Display.printStartScreen(term);
        
		while(term.getKey() != ' ')
			term.refreshScreen();
	}
	
	public void componentHidden(ComponentEvent e) {
    }
	
    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
    	updateView();
    }

    public void componentShown(ComponentEvent e) {

    }

	public void updateView ()
	{
        term.clearBuffer();

        int viewborder_x = term.width() / 4;
        int viewborder_y = term.height() / 4;


        if (viewcenter_x - term.width ()/2 + viewborder_x > player.pos().x())
        	viewcenter_x = player.pos().x() + term.width ()/2 - viewborder_x;
        if (viewcenter_x + term.width ()/2 - viewborder_x < player.pos().x())
        	viewcenter_x = player.pos().x() - term.width ()/2 + viewborder_x;
        if (viewcenter_y - term.height ()/2 + viewborder_y > player.pos().y())
        	viewcenter_y = player.pos().y() + term.height()/2 - viewborder_y;
        if (viewcenter_y + term.height ()/2 - viewborder_y < player.pos().y())
        	viewcenter_y = player.pos().y() - term.height()/2 + viewborder_y;

        for(int x = 0; x < term.width (); x++)
        {
            for(int y = 0; y < term.height (); y++)
            {
            	int worldx = viewcenter_x - term.width()/2 + x;
            	int worldy = viewcenter_y - term.height()/2 + y;
            	if (worldx < 0 || worldy < 0 || worldx >= world.width()
            		|| worldy >= world.height())
            		continue;
            	term.bufferChar(x, y, world.look(worldx, worldy));	
            }
        }
        term.refreshScreen();
	}
	
	public void run ()
	{
        while(!player.expired())
        {
        	updateView ();
            world.tick();
        }
	}
	
	public void finish () throws InterruptedException
	{
        Display.printEndScreen(term);

		while(term.getKey() != ' ')
			term.refreshScreen();
	}
	
    public static void main(String[] args)
    {
        try {
        	Rogue rogue = new Rogue ();
        	rogue.run ();
        	rogue.finish ();
        
        	System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
