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
        World world = new Level(80, 40, player);
        world.addActor(new Monster(ColoredChar.create('Z', Color.green)));
        term.registerCamera(player, 5, 5);
        
		Display.printStartScreen(term);
        
        while(term.getKey() != ' ')
        	term.refreshScreen();
        
        while(!player.expired())
        {
            term.clearBuffer();
            for(int x = 0; x < Math.min (term.width (), world.width ()); x++)
                for(int y = 0; y < Math.min (term.height (), world.height ()); y++)
                    term.bufferChar(x + 11, y, world.look(x, y));
            term.bufferCameras();
            term.refreshScreen();

            world.tick();
        }
        
        Display.printEndScreen(term);

        while(term.getKey() != ' ')
        	term.refreshScreen();
        
        System.exit(0);
    }
}
