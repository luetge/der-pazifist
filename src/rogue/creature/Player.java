package rogue.creature;

import jade.fov.RayCaster;
import jade.fov.ViewField;
import jade.ui.Camera;
import jade.ui.HUD;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Door;

import java.util.Collection;

import pazi.features.KeyboardFight;
import pazi.features.KeyboardWalk;
import pazi.features.PlayerBehaviour;

public class Player extends Creature implements Camera
{
    private ViewField fov;
    int counter = 0;
    int faith, rage;

    public Player()
    {
        super(ColoredChar.create('♓'), "Der PaziFist");
        fov = new RayCaster();
        min_d = 40;
        max_d = 70;
        setWalkBehaviour(new KeyboardWalk());
        setBehaviour(new PlayerBehaviour());
        setFightBehaviour(new KeyboardFight());
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
    public Collection<Coordinate> getViewField()
    {
    	if(world() == null)
    		return null;
        return fov.getViewField(world(), pos(), 10);
    }
    
    @Override
    public void takeDamage(int d) {
    	super.takeDamage(d);
    	HUD.setHP(hp);
    }
}
