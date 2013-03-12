package rogue.creature;

import jade.fov.RayCaster;
import jade.fov.ViewField;
import jade.util.datatype.Door;
import jade.ui.Camera;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

import java.util.Collection;

public class Player extends Creature implements Camera
{
    private ViewField fov;
    int counter = 0;

    public Player()
    {
        super(ColoredChar.create('♓'), "Der PaziFist");
        fov = new RayCaster();
        min_d = 40;
        max_d = 70;
        //TODO Singleton?
    }
    
    @Override
    public void setPos(int x, int y)
    {
    	Door door = world().getDoor(x,y);
    	if (door != null)
    	{
    		world().stepThroughDoor(door);
    	}
    	super.setPos(x,y);
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
    
    @Override
    public void neutralize() {
    	this.expire();
    }
}
