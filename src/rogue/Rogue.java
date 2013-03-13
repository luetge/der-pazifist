package rogue;

import jade.core.World;
import jade.core.Messenger.Message;
import jade.ui.HUD;
import jade.ui.TermPanel;
import jade.ui.TiledTermPanel;
import jade.ui.View;
import jade.util.Guard;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Door;
import jade.util.datatype.Direction;
import jade.util.datatype.Coordinate;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import pazi.Display;
import pazi.features.Braaaiiiiins;
import rogue.creature.Monster;
import rogue.creature.Player;
import rogue.level.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Rogue implements ComponentListener
{
	private TiledTermPanel term;
	private Player player;
	private Level level;
	private World world;
	private View view;
	boolean running;
	
	public Rogue () throws InterruptedException
	{
		running = false;
		term = TiledTermPanel.getFramedTerminal("Der PaziFist");

		term.loadTiles("res/tiles");
		
        
        player = new Player();
        level = new Level(256, 196, player, "mainworld");
        
        world = level.getWorld("mainworld");
        
        view = new View (player.pos ());
        
        Monster m;
        Braaaiiiiins brains = new Braaaiiiiins();
		for (int i = 0; i < 500; i++){
			m = new Monster(ColoredChar.create('Z', Color.green), "Blutiger Zombie");
			m.addFeatureAtTheEnd(brains);
			world.addActor(m);
		}
        
        term.addComponentListener(this);
        
		Display.printStartScreen(term);
        
		waitForSpace();
	}
	
	public void componentHidden(ComponentEvent e) {
    }
	
    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
    	if (running)
    		view.update (term, world, player);
    }

    public void componentShown(ComponentEvent e) {
    }

    public void run () throws InterruptedException
	{
    	running = true;
        while(!player.expired())
        {
        	view.update (term, world, player);
        	showMessages();
			world.setCurrentKey(term.getKey());
            Door door = world.tick();
            if (door != null)
            {
            	world.removeActor(player);
            	world = level.stepToWorld(world, door);
            	Guard.verifyState(world!=null);
            	Door destdoor = world.getDoor(door.getDestID());
            	Guard.verifyState(destdoor!=null);
            	world.addActor(player, destdoor.getDestination());
            }
        }
        
        showMessages();
        running = false;
	}
        
    private void showMessages() throws InterruptedException {
    	while(world.hasNextMessage()){
    		Message m = world.getNextMessage();
    		String source = m.source.getName();
    		if(source == "mainworld")
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
		HUD.setVisible(false);
        Display.printEndScreen(term);
        waitForSpace();
	}
	
    public static void main(String[] args)
    {
        try {
        	Rogue rogue = new Rogue ();
        	HUD.setVisible(true);
        	rogue.run ();
        	rogue.finish ();
        
        	System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
