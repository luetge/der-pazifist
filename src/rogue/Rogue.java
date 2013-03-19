package rogue;

import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.util.ResourceLoader;

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
			level.world().addActor((Item)WeaponFactory.createWeapon("shotgun"));
		}
		for (int i = 0; i < 20; i++) {
			level.world().addActor(CreatureFactory.createCreature("sniper1", level.world()));
		}
        
        
		view.displayScreen (new AsciiMap("res/start"));;
        
		waitForSpace();
		
		view.loadTiles();
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
    		String message = level.world().getMessage();
    		if (message != null)
    		{
    			ArrayList<String> text = new ArrayList<String>();
    			text.add(message);
    			Dialog.sayTop (null, text);
    		}
    		
    		view.update();
    		
			Dialog dialog = level.world().getActiveDialog();
			if (dialog != null)
			{
				level.world().setMessage(null);
				dialog.tick(level.world());
				view.drawWorld(level.world());
			}
			else
			{
				while (view.nextKey())
				{
					level.world().setMessage(null);
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
	
	public static File createTmpDir() {
		File basedir = new File(System.getProperty("java.io.tmpdir"));
		String baseName = System.currentTimeMillis() + "-";
		for (int counter = 0; counter < 1000; counter++)
		{
			File tempDir = new File (basedir, baseName + counter);
			if(tempDir.mkdir())
				return tempDir;
		}
		throw new IllegalStateException("Failed to create temporary directory.");
	}
	
	public static void deleteDir(File dir)
	{
		if (dir == null)
			return;
		for (File file : dir.listFiles())
		{
			if (file.isDirectory())
				deleteDir(file);
			file.delete();
		}
		dir.delete();
	}
	
	public static void extract_native_lib (String name, File dir) throws IOException
	{
		if (name.equals("lwjgl.dll"))
		{
			name = new StringBuffer(name).insert(5, System.getProperty("sun.arch.data.model")).toString();
		}
		InputStream input = ResourceLoader.getResourceAsStream("res/native/" + name);
		File file = new File(dir, name);
		if (!file.createNewFile())
			throw new IllegalStateException("Failed to extract native library.");
		FileOutputStream output = new FileOutputStream(file);
		byte[] buffer = new byte[4096];
		int len;
		while ((len = input.read(buffer)) != -1)
		{
			output.write(buffer, 0, len);
		}
	}

	public static void extract_native_libs (File dir)
	{
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceAsStream("res/native/files.txt")));
			String str;
			while ((str = reader.readLine()) != null)
			{
				extract_native_lib (str, dir);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static void main(String[] args)
    {
    	File tmpdir = null;
        try {
        	tmpdir = createTmpDir();
        	extract_native_libs (tmpdir);
        	System.setProperty("org.lwjgl.librarypath", tmpdir.getAbsolutePath());//new File("res/native").getAbsolutePath());
        	Rogue rogue = new Rogue ();
        	rogue.run ();
        	rogue.finish ();
        	deleteDir(tmpdir);
        
        	System.exit(0);
		} catch (Exception e) {
			deleteDir(tmpdir);
			e.printStackTrace();
		}
    }
}
