package rogue.creature;

import jade.core.Dialog;
import jade.fov.RayCaster;
import jade.fov.ViewField;
import jade.ui.Camera;
import jade.ui.EndScreen;
import jade.ui.HUD;
import jade.util.Lambda;
import jade.util.Lambda.FilterFunc;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;
import jade.util.datatype.Door;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import pazi.behaviour.KeyboardGeneral;
import pazi.behaviour.KeyboardWalk;
import pazi.behaviour.PlayerBehaviour;
import pazi.features.RoundhousePunch;
import pazi.features.VisionFeature;
import pazi.items.HealingPotion;
import pazi.items.Item;
import pazi.items.ItemFactory;
import pazi.weapons.IMeleeWeapon;
import pazi.weapons.IRangedCombatWeapon;

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
    boolean canUseVisionFeature, canUseRoundhousePunch, canUseMeditate, canUseRedemption;
    boolean godmode;

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
        meleeWeapon = (IMeleeWeapon) ItemFactory.createWeapon("fist", this);
        //TODO Singleton?
        
        roundhousePunch = new RoundhousePunch();
        this.addGeneralFeature(roundhousePunch);
        HUD.setWeaponLbl(meleeWeapon, rcWeapon);
        canUseMeditate = false;
        canUseRoundhousePunch = false;
        canUseVisionFeature = false;
        canUseRedemption = false;
        godmode = false;
    }
    
    public boolean getGodMode()
    {
    	return godmode;
    }
    
    public void setGodMode(boolean godmode)
    {
    	this.godmode = godmode;
    	if (this.godmode)
    		world().setMessage("GOD MODE ACTIVATED");
    	else
    		world().setMessage("GOD MODE DEACTIVATED");
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
    	if (godmode)
    		return;
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
		System.out.println(25*(lvl*lvl + 3*lvl));
		if (this.xp>= 25*(lvl*lvl + 3*lvl))
			levelUp();
		HUD.setXP(this.xp);	
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
		if (!canUseVisionFeature)
			return;
		if (!this.getFeatures(VisionFeature.class).isEmpty())
			return;
		if (faith >= 20){
			this.addGeneralFeature(new VisionFeature(this, 5, 20));
			increaseFaith(-20);
			this.appendMessage("Ich, ich.. ich kann sehen!", true);
		setHasActed(true);
		}
	}
	
	
	public void roundhousePunch(){
		if (!canUseRoundhousePunch)
			return;
		if (rage >= 80){
			roundhousePunch.punch(this);
			increaseRage(-80);
			setHasActed(true);
			this.appendMessage("roooOOAAAARRR!!!!", true);
		}
	}
	
	@Override
	public void setMeleeWeapon(IMeleeWeapon weapon) {
		if (weapon == null)
			super.setMeleeWeapon((IMeleeWeapon) ItemFactory.createWeapon("fist"));
		else
			super.setMeleeWeapon(weapon);
		HUD.setWeaponLbl(meleeWeapon, rcWeapon);
	}
	
	@Override
	public void setRCWeapon(IRangedCombatWeapon weapon) {
		super.setRCWeapon(weapon);
		HUD.setWeaponLbl(meleeWeapon, rcWeapon);
	}
	
	@Override
	public void killedSomeone(Creature creature) {
		this.gainXp(creature.getXp());
		increaseRage(20);
		increaseFaith(-20);
		String id = creature.getIdentifier();
		if(id.startsWith("bandit"))
			EndScreen.BanditKilled();
		else if (id.startsWith("zombie"))
			EndScreen.ZombieKilled();
		else if (id.startsWith("alien"))
			EndScreen.AlienKilled();
		else if (id.startsWith("sniper"))
			EndScreen.SniperKilled();
	}

	public void meditate() {
		if (!canUseMeditate)
			return;
		increaseFaith(10);
		increaseRage(-10);
		this.appendMessage("Ooooooommmmmmmmm. Die Meditation stärkt meinen Glauben.", true);
		setHasActed(true);
	}
	
	public void redeem() {
		if (faith <30)
			return;
		Monster target = world().getActorAt(Monster.class, this.pos());
		if (target == null)
			return;
		if (target.isPassable()){
			target.expire();
			gainHP(10);
			increaseFaith(-30);
			this.appendMessage("Ich nehme deine Sünden auf mich. Deine Seele wird nun Frieden finden.");
		}
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
		setHP(getHP() + 10);
		HUD.setHP(getHP(),this.maxHp);
		this.min_d += 5;
		this.max_d += 5;
		world().setMessage("Du has Level " + this.lvl + " erreicht.");
		world().appendMessage("Du has Level " + this.lvl + " erreicht.");
		if (lvl == 2){
			canUseVisionFeature = true;
			String str = "Ich habe gerade die göttliche Sicht erlernt, Papa sei Dank! ('1')";
			world().setActiveDialog(Dialog.createSimpleTextDialog("Der PaziFist", str));
			this.appendMessage(str);
		}
		if (lvl == 4){
			canUseMeditate = true;
			String str = "Ich kann nun meditieren. Zur Beruhigung und Stärkung meines Glaubens. ('2')";
			world().setActiveDialog(Dialog.createSimpleTextDialog("Der PaziFist", str));
			this.appendMessage(str);
		}
		if (lvl == 5){
			canUseRedemption = true;
			String str = "Mein Glaube ist nun so gefestigt, dass ich arme Seelen retten kann! ('3')";
			world().setActiveDialog(Dialog.createSimpleTextDialog("Der PaziFist", str));
			this.appendMessage(str);
		}
		if (lvl == 6){
			canUseRoundhousePunch = true;
			String str = "AAAAAHHHHHHHH! ROUNDHOUSEPUNCH freigeschaltet (bei mind. 80% Rage: '4')";
			world().setActiveDialog(Dialog.createSimpleTextDialog("Der PaziFist", str));
			this.appendMessage(str);
		}
		
	}

	public void showHelp() {
		ArrayList<String> helpList = new ArrayList<String>();
		helpList.add("Bewegen mit Pfeiltasten");
		helpList.add("Angreifen oder reden mit Gegenlaufen");
		helpList.add("Heiltrank benutzen: 'H'");
		if (canUseVisionFeature){
			helpList.add("Göttliche Sicht: '1' (20 Faith)");
				if (canUseMeditate){
					helpList.add("Meditation: '2'");
						if (canUseRedemption){
							helpList.add("Seele eines Toten erlösen: Auf seiner Leiche stehen und '3' (30 Faith)");
							if (canUseRoundhousePunch)
								helpList.add("Roundhousepunch: '4' (80 Rage)");
						}
				}
		}
		world().setActiveDialog(Dialog.createSimpleTextDialog(null, helpList));		
		
	}

}
