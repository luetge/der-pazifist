package rogue.creature;

import jade.fov.RayCaster;
import jade.fov.ViewField;
import jade.ui.Camera;
import jade.ui.HUD;
import jade.util.Lambda;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;
import jade.util.datatype.Door;

import java.awt.Color;
import java.util.Collection;

import pazi.behaviour.KeyboardFight;
import pazi.behaviour.KeyboardGeneral;
import pazi.behaviour.KeyboardWalk;
import pazi.behaviour.PlayerBehaviour;
import pazi.items.HealingPotion;
import pazi.items.Item;

public class Player extends Creature implements Camera
{
    private ViewField fov;
    int counter = 0;
    int faith, rage;
    private int radius;
    
    ColoredChar facesets[][];
    int currentfaceset;

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
        radius = 10;
        setWalkBehaviour(new KeyboardWalk());
        setBehaviour(new PlayerBehaviour());
        addGeneralFeature(new KeyboardGeneral());
        setCloseCombatBehaviour(new KeyboardFight());
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
        return fov.getViewField(world(), pos(), radius);
    }
    
    @Override
    public void takeDamage(int d,Creature source) {
    	super.takeDamage(d,source);
    	setHP(getHP() - d);
    }   

	@Override
	public int getGold(int amount) {
		int ret = super.getGold(amount);
		HUD.setGold(inventory.getGold());	
		return ret;
	}
	
	protected void setHP(int hp){
		super.setHP(hp);
		HUD.setHP(getHP(),this.maxHp);
	}


	public void setViewFieldRadius(int radius) {
		this.radius = radius;
	}

	public int getViewFieldRadius() {
		return radius;
	}


	public void drinkHealingPotion() {
		for(HealingPotion potion : Lambda.filterType(getInventory().getItems(), HealingPotion.class)) {
			useItem(potion);
			return;
		}
		appendMessage("Ich habe leider keine Tränke mehr! :(");
	}
	
	public void gainXp(int xp){
		System.out.println(this.xp);
		this.xp += xp;
		System.out.println(this.xp);
		if (this.xp>=lvl*100)
			levelUp();
		//System.out.println(""+xp+"/"+lvl);
			
	}

	public void levelUp(){
		this.lvl += 1;
		HUD.setLevel(this.lvl);
		this.maxHp += 10;
		this.hp=this.maxHp;
		HUD.setHP(getHP(),this.maxHp);
		this.min_d += 5;
		this.max_d += 5;
	}
}
