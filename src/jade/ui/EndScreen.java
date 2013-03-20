package jade.ui;

import java.awt.Color;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import jade.gen.map.AsciiMap;
import jade.util.datatype.Coordinate;

public class EndScreen {
	
	AsciiMap map;
	
	private static int numzombies = 0;
	private static int numaliens = 0;
	private static int numbandits = 0;
	private static int numsnipers = 0;
	
	public EndScreen(String name)
	{
		map = new AsciiMap(name);
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
	
	private void print (Coordinate coord, Integer variable, int length)
	{
		View view = View.get();
		String str = variable.toString();
		while (str.length() < length)
			str = ' ' + str;
		view.drawString(coord.x(), coord.y(), 1.0f, str, Color.white);
	}
	
	public void display()
	{
		boolean running = true;
		View view = View.get();
		while (running)
		{
			view.clear();

			map.render(view, 0, 0);
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
				}
			}
			view.update();
			while (view.nextKey())
			{
				if (view.getKeyEvent() == Keyboard.KEY_SPACE)
				{
					running = false;
					break;
				}
			}
		}
	}
}
