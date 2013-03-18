package rogue.creature;

import jade.fov.RayCaster;
import jade.fov.ViewField;
import jade.ui.Camera;
import jade.ui.HUD;
import jade.util.Lambda;
import jade.util.Lambda.FilterFunc;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;
import jade.util.datatype.Door;

import java.awt.Color;
import java.util.Collection;

import pazi.behaviour.KeyboardGeneral;
import pazi.behaviour.KeyboardWalk;
import pazi.behaviour.PlayerBehaviour;
import pazi.features.RoundhousePunch;
import pazi.features.VisionFeature;
import pazi.items.HealingPotion;
import pazi.items.Item;
import pazi.weapons.IMeleeWeapon;
import pazi.weapons.WeaponFactory;

public class Player extends Creature implements Camera
{
    private ViewField fov;
    int counter = 0;
    int faith, rage;
    int maxFaith, maxRage;
    private int radius;
	RoundhousePunch roundhousePunch;
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
        faith = 100;
        maxRage = 100;
        rage = 0;
        maxFaith = 100;
        updateFaith();
        updateRage();
        updateHP();
        setWalkBehaviour(new KeyboardWalk());
        setBehaviour(new PlayerBehaviour());
        addGeneralFeature(new KeyboardGeneral());
        meleeWeapon = (IMeleeWeapon) WeaponFactory.createWeapon("fist", this);
        //TODO Singleton?
        
        roundhousePunch = new RoundhousePunch();
        this.addGeneralFeature(roundhousePunch);
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
         double rand = Math.random();
         if (rand < 0.5){
        	 increaseRage(-1);
        	 if (rand < 0.05)
        		 increaseFaith(1);
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
    public void takeDamage(int d, Creature source) {
    	super.takeDamage(d, source);
    	updateHP();
    }   

	@Override
	public int getGold(int amount) {
		int ret = super.getGold(amount);
		HUD.setGold(inventory.getGold());	
		return ret;
	}
	
	protected void setHP(int hp){
		super.setHP(hp);
		updateHP();
	}
	
	protected void updateHP(){
		HUD.setHP(getHP(), this.maxHp);
	}
	
	protected void updateFaith(){
		HUD.setFaith(faith);
	}
	
	protected void updateRage(){
		HUD.setRage(rage);
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
		this.xp += xp;
		if (this.xp>=lvl*100)
			levelUp();
			
	}

	public Iterable<Creature> getCreaturesInViewfield() {
		final Collection<Coordinate> coords = fov.getViewField(world(), pos(), radius);
		return Lambda.filter(world().getActors(Creature.class), new FilterFunc<Creature>() {
			@Override
			public boolean filter(Creature element) {
				return element.getHP() > 0 && element.getFace().ch() != ' ' && !Player.class.isAssignableFrom(element.getClass()) && coords.contains(element.pos());
			}
		});
	}

	public void increaseFOV() {
		if (!this.getFeatures(VisionFeature.class).isEmpty())
			return;
		if (faith >= 20){
			this.addGeneralFeature(new VisionFeature(this, 5, 20));
			increaseFaith(-20);
		setHasActed(true);
		}
	}
	
	
	public void roundhousePunch(){
		if (rage >= 80){
			roundhousePunch.punch(this);
			increaseRage(-80);
			setHasActed(true);
			this.appendMessage("roooOOAAAARRR!!!!");
		}
	}
	

	public void killedSomeone(Creature creature) {
		increaseRage(20);
		increaseFaith(-20);
	}

	public void meditate() {
		increaseFaith(10);
		increaseRage(-10);
		this.appendMessage("Ooooooommmmmmmmm. Die Meditation stärkt meinen Glauben.");
		setHasActed(true);
	}

	private void increaseFaith(int i) {
		faith += i;
		if (faith > maxFaith)
			faith = maxFaith;
		else if (faith < 0)
			faith = 0;
		updateFaith();
	}

	private void increaseRage(int i) {
		rage += i;
		if (rage > maxRage)
			rage = maxRage;
		else if (rage < 0)
			rage = 0;
		updateRage();
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
