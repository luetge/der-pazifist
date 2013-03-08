package rogue.creature;

import jade.fov.RayCaster;
import jade.fov.ViewField;
import jade.ui.Camera;
import jade.ui.Terminal;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

import java.util.Collection;

import pazi.features.Walking;

public class Player extends Creature implements Camera
{
    private Terminal term;
    private ViewField fov;
    private int last_key;

    public Player(Terminal term)
    {
        super(ColoredChar.create('♓'));
        this.term = term;
        fov = new RayCaster();
        features.add(new Walking());
    }

    @Override
    public void act()
    {
        try
        {
            last_key = term.getKey();
            super.act();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void walk() {
    	 Direction dir = Direction.keyToDir(last_key);
         if(dir != null)
             move(dir);
    };

    @Override
    public Collection<Coordinate> getViewField()
    {
        return fov.getViewField(world(), pos(), 5);
    }
}
