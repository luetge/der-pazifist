package rogue;

import jade.core.Messenger.Message;
import jade.core.World;
import jade.ui.TiledTermPanel;
import jade.ui.View;
import jade.util.datatype.ColoredChar;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import pazi.Display;
import rogue.creature.Monster;
import rogue.creature.Player;
import rogue.level.Level;

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
        
        player = new Player();
        world = new Level(256, 196, player, "Test-Level");
        
        view = new View (player.pos ());
        
		for (int i = 0; i < 600; i++)
			world.addActor(new Monster(ColoredChar.create('Z', Color.green), "Blutiger Zombie"));
        
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

    public void run () throws InterruptedException
	{
        while(!player.expired())
        {
        	view.update (term, world, player);
        	Message m = world.getNextMessage();
        	if(m != null){
        		String source = m.source.getName();
        		if (source == "Test-Level")				// TODO sehr haesslich
        			source = "Gott: ";
        			else
        				source += ": ";	
        				
        		term.setCurrentConsoleText(source + m.text + (world.hasNextMessage() ? " (mehr)" : ""));
        		if(world.hasNextMessage()){ // Leertaste schaltet zu nächstem Text um
        			waitForSpace();
        			continue;
        		}
        	}
        	else
        		term.setCurrentConsoleText("");
			world.setCurrentKey(term.getKey());
            world.tick();
        }
	}
        
    public void waitForSpace() throws InterruptedException{
        	while(term.getKey() != ' ')
        		term.refreshScreen();
        }
	
	public void finish () throws InterruptedException
	{
        Display.printEndScreen(term);
        waitForSpace();
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
