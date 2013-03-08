package jade.ui;

import java.util.Collection;

import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import java.awt.Color;
import jade.core.World;
import rogue.creature.Player;


public class View {
	private int center_x, center_y;
	
	public View (Coordinate center)
	{
		this.center_x = center.x();
		this.center_y = center.y();
	}
	
	public void update (TiledTermPanel term, World world, Player player)
	{
        term.clearBuffer();

        int viewborder_x = term.width() / 4;
        int viewborder_y = term.height() / 4;


        if (center_x - term.width ()/2 + viewborder_x > player.pos().x())
        	center_x = player.pos().x() + term.width ()/2 - viewborder_x;
        if (center_x + term.width ()/2 - viewborder_x < player.pos().x())
        	center_x = player.pos().x() - term.width ()/2 + viewborder_x;
        if (center_y - term.height ()/2 + viewborder_y > player.pos().y())
        	center_y = player.pos().y() + term.height()/2 - viewborder_y;
        if (center_y + term.height ()/2 - viewborder_y < player.pos().y())
        	center_y = player.pos().y() - term.height()/2 + viewborder_y;
        
        Collection<Coordinate> viewfield = player.getViewField ();

        for(int x = 0; x < term.width (); x++)
        {
        	// Eine Zeile auslassen für Textausgabe
            for(int y = 1; y < term.height (); y++)
            {
            	int worldx = center_x - term.width()/2 + x;
            	int worldy = center_y - term.height()/2 + y;
            	if (worldx < 0 || worldy < 0 || worldx >= world.width()
            		|| worldy >= world.height())
            		continue;
            	ColoredChar c = world.look(worldx, worldy);
        		if (!viewfield.contains(new Coordinate (worldx, worldy)))
            	{
        			if (!world.isAlwaysVisible (worldx, worldy))
        			{
            			continue;
            		}
        			else
        			{
        				c = ColoredChar.create (c.ch (), c.color().darker());
        			}
            	}
        		else
        		{
        			Color bgcolor = c.bgcolor();
        			if (bgcolor == Color.black)
        				bgcolor = Color.darkGray;
        			c = ColoredChar.create (c.ch (), c.color(), bgcolor.brighter());
        		}
            	term.bufferChar(x, y, c);	
            }
        }
        term.refreshScreen();
	}
}