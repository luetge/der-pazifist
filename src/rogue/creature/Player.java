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

import pazi.behaviour.KeyboardFight;
import pazi.behaviour.KeyboardWalk;
import pazi.behaviour.PlayerBehaviour;
import pazi.items.Item;

public class Player extends Creature implements Camera
{
    private ViewField fov;
    int counter = 0;
    int faith, rage;
    
    ColoredChar facesets[][];
    int currentfaceset;
    int gold = 0;

    public Player()
    {
        super(ColoredChar.create('♓', new Color(0xFFFFF0)), "Der PaziFist");
        facesets = new ColoredChar[2][9];
        for (int i = 0; i < 9; i++)
        {
        	facesets[0][i] = ColoredChar.create('♓', new Color(0xFFFFF0 + i));
        	facesets[1][i] = ColoredChar.create('♓', new Color(0xFFFEF0 + i));
        }
    	currentfaceset = 0;
        setFaces (facesets[currentfaceset]);
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
    public void talkto(Ally ally)
    {
    	ally.startDialog ();
    }

    @Override	
    public void walk() {
        currentfaceset++;
        if (currentfaceset >= facesets.length)
       	 currentfaceset = 0;

        Direction dir = Direction.keyToDir(world().getCurrentKey());
         if(dir != null)
         {
        	 setFace (facesets[currentfaceset][dir.getID()]);
             move(dir);
         }
         else
         {
        	 setFace (facesets[currentfaceset][4]);
         }
         
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
