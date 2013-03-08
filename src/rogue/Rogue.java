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

import jade.ui.View;

public class Rogue implements ComponentListener
{
	private TiledTermPanel term;
	private Player player;
	private World world;
	private View view;
	
	Rogue () throws InterruptedException
	{
		term = TiledTermPanel.getFramedTerminal("Der PaziFist");
        term.registerTile("dungeon.png", 5, 59, ColoredChar.create('#'));
        term.registerTile("dungeon.png", 3, 60, ColoredChar.create('.'));
        term.registerTile("dungeon.png", 5, 20, ColoredChar.create('@'));
        term.registerTile("dungeon.png", 14, 30, ColoredChar.create('D', Color.red));
        
        player = new Player(term);
        world = new Level(256, 196, player);
        
        view = new View (player.pos ());
        
		for (int i = 0; i < 600; i++)
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
    	view.update (term, world, player);
    }

    public void componentShown(ComponentEvent e) {
    }

    public void run ()
	{
        while(!player.expired())
        {
        	view.update (term, world, player);
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
