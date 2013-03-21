package jade.ui;

import java.awt.Color;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import jade.gen.map.AsciiMap;
import jade.util.datatype.Coordinate;

public class EndScreen {
	
	AsciiMap loosemap;
	AsciiMap winmap;
	
	private static int numzombies = 0;
	private static int numaliens = 0;
	private static int numbandits = 0;
	private static int numsnipers = 0;
	private static int numnazis = 0;
	private static boolean hitlerkilled = false;
	private static String killerid = null;
	
	private static String nokillerstr = "Gott";
	private static String nokillactionstr = "erschlagen, weil er aufgegeben hat.";

	private static String zombiekillerstr = "einem verrottenden Zombie";
	private static String zombiekillactionstr = "gefressen";
	private static String alienkillerstr = "einem ekligen Schleimalien";
	private static String alienkillactionstr = "getötet";
	private static String banditkillerstr = "einem jämmerlichen Bandit";
	private static String banditkillactionstr = "gekopfnusst - und schämt sich";
	private static String sniperkillerstr = "einem hinterhältigen Sniper";
	private static String sniperkillactionstr = "erschossen";
	private static String nazikillerstr = "einem Nazi";
	private static String nazikillactionstr = "getötet";
	private static String hitlerkillerstr = "Hitler";
	private static String hitlerkillactionstr = "erlegt";
	
	public EndScreen(String loosename, String winname)
	{
		loosemap = new AsciiMap(loosename);
		winmap = new AsciiMap(winname);
	}
	
	public static void SetKiller (String id)
	{
		killerid = id;
	}
	
	public static void HitlerKilled()
	{
		hitlerkilled = true;
	}
	
	public static void ZombieKilled()
	{
		numzombies++;
	}
	
	public static void AlienKilled()
	{
		numaliens++;
	}
	
	public static void BanditKilled()
	{
		numbandits++;
	}
	
	public static void SniperKilled()
	{
		numsnipers++;
	}
	
	public static void NazisKilled()
	{
		numnazis++;
	}
	
	private void print (Coordinate coord, Integer variable, int length)
	{
		View view = View.get();
		String str = variable.toString();
		while (str.length() < length)
			str = ' ' + str;
		view.drawString(coord.x(), coord.y(), 1.0f, str, Color.white);
	}
	
	void printkiller (Set<Coordinate> coords, String str)
	{
		if (coords == null)
			return;
		for (Coordinate coord : coords)
		{
			View.get().drawString(coord.x(), coord.y(), 1.0f, str, Color.red);
		}
	}

	void printkillaction (Set<Coordinate> coords, String str)
	{
		if (coords == null)
			return;
		for (Coordinate coord : coords)
		{
			View.get().drawString(coord.x(), coord.y(), 1.0f, str, Color.white);
		}
	}
	
	public void display()
	{
		boolean running = true;
		View view = View.get();
		view.resetCloseRequested();
		while (running)
		{
			view.clear();

			AsciiMap map = hitlerkilled?winmap:loosemap;
			
			map.render(view, 0, 0);
			
			Set<Coordinate> killercoords = map.getSpecialCoords("killer");
			Set<Coordinate> killactioncoords = map.getSpecialCoords("killaction");
			
			if (killerid == null) {
				printkiller (killercoords, nokillerstr);
				printkillaction (killactioncoords, nokillactionstr);
			} else if (killerid.startsWith("zombie")) {
				printkiller (killercoords, zombiekillerstr);
				printkillaction (killactioncoords, zombiekillactionstr);
			} else if (killerid.startsWith("alien")) {
				printkiller (killercoords, alienkillerstr);
				printkillaction (killactioncoords, alienkillactionstr);
			} else if (killerid.startsWith("bandit")) {
				printkiller (killercoords, banditkillerstr);
				printkillaction (killactioncoords, banditkillactionstr);
			} else if (killerid.startsWith("sniper")) {
				printkiller (killercoords, sniperkillerstr);
				printkillaction (killactioncoords, sniperkillactionstr);
			} else if (killerid.startsWith("nazi")) {
				printkiller (killercoords, nazikillerstr);
				printkillaction (killactioncoords, nazikillactionstr);
			} else if (killerid.equals("hitler")) {
				printkiller (killercoords, hitlerkillerstr);
				printkillaction (killactioncoords, hitlerkillactionstr);
			}
			
			for (String special : map.getSpecials())
			{
				String args[] = special.split(":");
				if (args.length != 2)
					continue;
				if (args[0].equals("numzombies"))
				{
					int len = Integer.parseInt(args[1]);
					for (Coordinate coord : map.getSpecialCoords(special))
					{
						print(coord, numzombies, len);
					}
				} else if (args[0].equals("numaliens")) {
					int len = Integer.parseInt(args[1]);
					for (Coordinate coord : map.getSpecialCoords(special))
					{
						print(coord, numaliens, len);
					}
				} else if (args[0].equals("numbandits")) {
					int len = Integer.parseInt(args[1]);
					for (Coordinate coord : map.getSpecialCoords(special))
					{
						print(coord, numbandits, len);
					}
				} else if (args[0].equals("numsnipers")) {
					int len = Integer.parseInt(args[1]);
					for (Coordinate coord : map.getSpecialCoords(special))
					{
						print(coord, numsnipers, len);
					}				
				} else if (args[0].equals("numnazis")) {
					int len = Integer.parseInt(args[1]);
					for (Coordinate coord : map.getSpecialCoords(special))
					{
						print(coord, numnazis, len);
					}				
				}
			}
			view.update();
			while (view.nextKey() && !view.closeRequested())
			{
				if (view.getKeyEvent() == Keyboard.KEY_SPACE)
				{
					running = false;
					break;
				}
			}
			if (view.closeRequested())
				return;
		}
	}
}
