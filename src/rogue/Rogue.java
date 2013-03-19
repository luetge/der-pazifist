package rogue;

import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;

import jade.core.Dialog;
import jade.core.Messenger.Message;
import jade.gen.map.AsciiMap;
import jade.ui.Bagpack;
import jade.ui.HUD;
import jade.ui.Log;
import jade.ui.View;
import jade.util.datatype.Door;

import pazi.items.HealingPotion;
import pazi.items.Item;
import pazi.weapons.WeaponFactory;
import rogue.creature.CreatureFactory;
import rogue.creature.Player;
import rogue.level.Level;

public class Rogue
{
	private Player player;
	private Level level;
	private View view;
	boolean running;
	
	public Rogue () throws InterruptedException
	{
		running = false;

        View.create("Der Pazifist");
        view = view.get();
        HUD.init();

        player = new Player();
        level = new Level(256, 192, player, "mainworld");

        view.setCenter(player.pos());
        
		for (int i = 0; i < 100; i++){
			level.world().addActor(CreatureFactory.createCreature("zombie1", level.world()));
			level.world().addActor(CreatureFactory.createCreature("bandit2", level.world()));
			level.world().addActor(CreatureFactory.createCreature("alien1", level.world()));
			level.world().addActor(new HealingPotion());
			level.world().addActor((Item)WeaponFactory.createWeapon("knuckleduster"));
		}
		for (int i = 0; i < 20; i++) {
			level.world().addActor(CreatureFactory.createCreature("sniper1", level.world()));
		}
        
        
		view.displayScreen (new AsciiMap("res/start"));;
        
		waitForSpace();
		
		view.loadTiles("res/tiles");
	}
	
    public void run () throws InterruptedException
	{
    	running = true;
    	Log.showLogFrame(true);
    	Bagpack.showBPFrame(true);
    	HUD.setVisible(true);
    	{
    		Point loc = HUD.getFrame().getLocation();
    		loc.x += HUD.getFrame().getWidth();
        	view.getFrame().setLocation (loc);
        	HUD.getFrame().setSize(HUD.getFrame().getWidth(), view.getFrame().getHeight());
        	
            Log.getLogFrame().setLocation(0, view.getFrame().getHeight()-4);
            Log.getLogFrame().setSize(view.getFrame().getWidth() + HUD.getFrame().getWidth(), Log.getLogFrame().getPreferredSize().height);
            Bagpack.getBPFrame().setLocation(view.getFrame().getWidth() + HUD.getFrame().getWidth(), 0);
            int maxwidth = Toolkit.getDefaultToolkit().getScreenSize().width - view.getFrame().getWidth() - HUD.getFrame().getWidth();
            Bagpack.getBPFrame().setSize(Math.min(maxwidth, 400),
            		Log.getLogFrame().getBounds().y + Log.getLogFrame().getBounds().height);
    	}

		view.drawWorld(level.world());
    	HUD.setCreatures(player.getCreaturesInViewfield());
		showMessages();
    	while (!player.expired() && !view.closeRequested())
    	{
    		view.drawWorld(level.world());
    		view.update();

			Dialog dialog = level.world().getActiveDialog();
			if (dialog != null)
			{
				dialog.tick(level.world());
				view.drawWorld(level.world());
			}
			else
			{
				while (view.nextKey())
				{
    				level.world().setCurrentKey(view.getKeyEvent());
    				Door door = level.world().tick();
    				if (door != null)
    					level.stepThroughDoor(door);
    				view.drawWorld(level.world());
    	        	HUD.setCreatures(player.getCreaturesInViewfield());
    				showMessages();
    			}

    		}
    	}
        running = false;
	}
        
    private void showMessages() throws InterruptedException {
    	while(level.world().hasNextMessage()){
    		Message m = level.world().getNextMessage();
    		String source = m.source.getName();
    		if(source == "mainworld")
    			source = "Gott: ";
    		else
    			source += ": ";
    		String sText = source + m.text;
    		Log.addMessage(sText);
    	}
	}

	public void waitForSpace() throws InterruptedException{
    }
	
	public void finish () throws InterruptedException
	{
		view.clearTiles();
		view.displayScreen(new AsciiMap("res/end"));
        waitForSpace();
	}
	
    public static void main(String[] args)
    {
        try {
        	System.setProperty("org.lwjgl.librarypath", new File("res/native").getAbsolutePath());
        	Rogue rogue = new Rogue ();
        	rogue.run ();
        	rogue.finish ();
        
        	System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
