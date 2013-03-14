package rogue;

import jade.core.Messenger.Message;
import jade.core.Dialog;
import jade.ui.HUD;
import jade.ui.Log;
import jade.ui.TiledTermPanel;
import jade.ui.View;
import jade.util.Guard;
import jade.util.datatype.Door;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import pazi.Display;
import rogue.creature.CreatureFactory;
import rogue.creature.Player;
import rogue.level.Level;

public class Rogue implements ComponentListener
{
	private TiledTermPanel term;
	private Player player;
	private Level level;
	private View view;
	boolean running;
	
	public Rogue () throws InterruptedException
	{
		running = false;
		term = TiledTermPanel.getFramedTerminal("Der PaziFist");

		term.loadTiles("res/tiles");
		
        player = new Player();
        level = new Level(256, 196, player, "mainworld");
        
        view = new View (player.pos ());
        
		for (int i = 0; i < 100; i++){
			level.world().addActor(CreatureFactory.createCreature("zombie1", level.world()));
			level.world().addActor(CreatureFactory.createCreature("bandit2", level.world()));
			level.world().addActor(CreatureFactory.createCreature("alien1", level.world()));
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
    		view.update (term, level.world(), player);
    }

    public void componentShown(ComponentEvent e) {
    }

    public void run () throws InterruptedException
	{
    	running = true;
    	Log.showLogFrame(true);
        while(!player.expired())
        {
        	view.update (term, level.world(), player);
        	
        	Dialog dialog = level.world().getActiveDialog();
        	
        	if (dialog != null)
        	{
        		dialog.tick(level.world());
        	}
        	else
        	{
        		showMessages();
        		level.world().setCurrentKey(term.getKey());
        		Door door = level.world().tick();
        		if (door != null)
        		{
        			level.stepThroughDoor(door);
        		}
        	}
        }
        
        showMessages();
        Log.showLogFrame(false);
        running = false;
	}
        
    private void showMessages() throws InterruptedException {
    	term.setCurrentConsoleText("");
    	while(level.world().hasNextMessage()){
    		Message m = level.world().getNextMessage();
    		String source = m.source.getName();
    		if(source == "mainworld")
    			source = "Gott: ";
    		else
    			source += ": ";
    		String sText = source + m.text;
    		if(m.important)
    			term.setCurrentConsoleText(sText);
//    			term.setCurrentConsoleText(sText + (level.world().hasNextMessage() ? " (mehr)" : ""));
    		Log.addMessage(sText);
    		term.refreshScreen();
//    		if(m.important && level.world().hasNextMessage())
//    			waitForSpace();
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
