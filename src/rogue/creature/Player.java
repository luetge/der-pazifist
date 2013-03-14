package rogue.creature;

import jade.fov.RayCaster;
import jade.fov.ViewField;
import jade.ui.Camera;
import jade.ui.HUD;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;
import jade.util.datatype.Door;

import java.awt.Color;
import java.util.Collection;

import pazi.features.KeyboardFight;
import pazi.features.KeyboardWalk;
import pazi.features.PlayerBehaviour;
import pazi.items.Item;

public class Player extends Creature implements Camera
{
    private ViewField fov;
    int counter = 0;
    int faith, rage;
    
    ColoredChar faces[];
    int currentface;
    int gold = 0;

    public Player()
    {
        super(ColoredChar.create('♓'), "Der PaziFist");
    	faces = new ColoredChar[2];
    	faces[0] = ColoredChar.create('♓', new Color(0xFFFFFF));
    	faces[1] = ColoredChar.create('♓', new Color(0xFEFEFE));
    	currentface = 0;
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
    	Item item = world().getActorAt(Item.class, x, y);
    	if (item != null)
    			item.getPickedUp(this);
    }

    @Override	
    public void walk() {
    	 Direction dir = Direction.keyToDir(world().getCurrentKey());
         if(dir != null)
             move(dir);
         
         currentface++;
         if (currentface >= faces.length)
        	 currentface = 0;
         setFace(faces[currentface]);
    };

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

	public void getGold(int amount) {
    	gold+=amount;
    	HUD.setGold(gold);	
	}

}
