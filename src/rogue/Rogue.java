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
import pazi.features.Braaaiiiiins;
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
        
        Monster m;
        Braaaiiiiins brains = new Braaaiiiiins();
		for (int i = 0; i < 600; i++){
			m = new Monster(ColoredChar.create('Z', Color.green), "Blutiger Zombie");
			m.addFeatureAtTheEnd(brains);
			world.addActor(m);
		}
        
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
        	showMessages();
			world.setCurrentKey(term.getKey());
            world.tick();
        }
        
        showMessages();
	}
        
    private void showMessages() throws InterruptedException {
    	term.setCurrentConsoleText("");
    	while(world.hasNextMessage()){
    		Message m = world.getNextMessage();
    		String source = m.source.getName();
    		if(source == "Test-Level")
    			source = "Gott: ";
    		else
    			source += ": ";
    		term.setCurrentConsoleText(source + m.text + (world.hasNextMessage() ? " (mehr)" : ""));
    		term.refreshScreen();
    		if(world.hasNextMessage())
    			waitForSpace();
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
