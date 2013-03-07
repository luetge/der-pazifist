package rogue;

import jade.core.World;
import jade.ui.TiledTermPanel;
import jade.util.datatype.ColoredChar;

import java.awt.Color;

import pazi.Display;
import rogue.creature.Monster;
import rogue.creature.Player;
import rogue.level.Level;

public class Rogue
{
    public static void main(String[] args) throws InterruptedException
    {
        TiledTermPanel term = TiledTermPanel.getFramedTerminal("Der PaziFist");
        term.registerTile("dungeon.png", 5, 59, ColoredChar.create('#'));
        term.registerTile("dungeon.png", 3, 60, ColoredChar.create('.'));
        term.registerTile("dungeon.png", 5, 20, ColoredChar.create('@'));
        term.registerTile("dungeon.png", 14, 30, ColoredChar.create('D', Color.red));
        
        Player player = new Player(term);
        World world = new Level(256, 196, player);
        world.addActor(new Monster(ColoredChar.create('Z', Color.green)));
        
		Display.printStartScreen(term);
        
        while(term.getKey() != ' ')
        	term.refreshScreen();
        
		int viewcenter_x = player.pos().x();
		int viewcenter_y = player.pos().y();
        
        while(!player.expired())
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

            world.tick();
        }
        
        Display.printEndScreen(term);

        while(term.getKey() != ' ')
        	term.refreshScreen();
        
        System.exit(0);
    }
}
