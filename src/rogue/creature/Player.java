package rogue.creature;

import jade.fov.RayCaster;
import jade.fov.ViewField;
import jade.ui.Camera;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

import java.util.Collection;

import pazi.features.Walking;

public class Player extends Creature implements Camera
{
    private ViewField fov;
    int counter = 0;

    public Player()
    {
        super(ColoredChar.create('♓'), "Der PaziFist");
        fov = new RayCaster();
        //TODO Singleton?
        features.add(new Walking());
    }

    @Override
    public void walk() {
    	 Direction dir = Direction.keyToDir(world().getCurrentKey());
         if(dir != null)
             move(dir);
    };

    @Override
    public Collection<Coordinate> getViewField()
    {
        return fov.getViewField(world(), pos(), 10);
    }
}
