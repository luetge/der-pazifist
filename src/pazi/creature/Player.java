package pazi.creature;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import pazi.behaviour.KeyboardGeneral;
import pazi.behaviour.KeyboardWalk;
import pazi.behaviour.PlayerBehaviour;
import pazi.core.Dialog;
import pazi.features.RecordSteps;
import pazi.features.RoundhousePunch;
import pazi.features.VisionFeature;
import pazi.fov.RayCaster;
import pazi.fov.ViewField;
import pazi.items.HealingPotion;
import pazi.items.Item;
import pazi.items.ItemFactory;
import pazi.ui.EndScreen;
import pazi.ui.HUD;
import pazi.util.Lambda;
import pazi.util.Lambda.FilterFunc;
import pazi.util.datatype.ColoredChar;
import pazi.util.datatype.Coordinate;
import pazi.util.datatype.Direction;
import pazi.util.datatype.Door;
import pazi.weapons.IMeleeWeapon;
import pazi.weapons.IRangedCombatWeapon;
import pazi.weapons.KnuckleDuster;
import pazi.weapons.MeleeWeaponPrototype;
import pazi.weapons.RCWeaponPrototype;
import pazi.weapons.Shotgun;
import pazi.weapons.SniperRifle;

public class Player extends Creature
{
    private ViewField fov;
    int counter = 0;
    int faith, rage;
    int maxFaith, maxRage;
    int maxHP = 100;
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
        addGeneralFeature(new RecordSteps());
        meleeWeapon = (IMeleeWeapon) ItemFactory.createWeapon("fist", this);
        
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

    public Collection<Coordinate> getViewField()
    {
    	if(world() == null)
    		return null;
        return fov.getViewField(world(), pos(), radius);
    }
    
    @Override
    public void takeDamage(int d, Creature source, boolean melee) {
    	if (godmode)
    		return;
    	super.takeDamage(d, source, melee);
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
		if(this.hp > this.maxHP)
			this.hp = maxHP;
		updateHP();
	}
	
	protected void updateHP(){
		HUD.setHP(getHP(), this.maxHP);
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
		if (this.xp>= 25*(lvl*lvl + 3*lvl))
			levelUp();
		HUD.setXP(this.xp);	
	}

	public Iterable<Monster> getMonstersInViewfield() {
		final Collection<Coordinate> coords = fov.getViewField(world(), pos(), radius);
		return Lambda.filter(world().getActors(Monster.class), new FilterFunc<Monster>() {
			@Override
			public boolean filter(Monster element) {
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
		{
			boolean foundfist = false;
			for (Item item : getInventory().getItems())
			{
				if (item.getIdentifier().equals("fist"))
				{
					meleeWeapon = (IMeleeWeapon)item;
					getInventory().removeItem(item);
					foundfist = true;
					break;
				}
			}
			if (!foundfist)
				meleeWeapon = (IMeleeWeapon) ItemFactory.createWeapon("fist");
		}
		else{
			if (meleeWeapon != null)
				inventory.addItem((Item)meleeWeapon);
			meleeWeapon = weapon;
		}
		HUD.setWeaponLbl(meleeWeapon, rcWeapon);
	}
	
	@Override
	public void setRCWeapon(IRangedCombatWeapon weapon) {
		super.setRCWeapon(weapon);
		refreshWeaponsHUD();
	}
	
	@Override
	public void killedSomeone(Creature creature) {
		if (creature.getClass().isAssignableFrom(DestructableObject.class))
			return;
			
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
		else if (id.startsWith("nazi"))
			EndScreen.NazisKilled();
		else if (id.equals("hitler"))
		{
			EndScreen.HitlerKilled();
			expire();
		}
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
			gainHP(30);
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
		this.maxHP += 10;
		this.addHP(20);
		HUD.setHP(getHP(),this.maxHP);
		this.min_d += 5;
		this.max_d += 5;
		world().setMessage("Du has Level " + this.lvl + " erreicht.");
		world().appendMessage("Du has Level " + this.lvl + " erreicht.");
		if (lvl == 3){
			canUseVisionFeature = true;
			String str = "(Lvl 3) Ich habe gerade die göttliche Sicht erlernt, Papa sei Dank! ('1')";
			world().setActiveDialog(Dialog.createSimpleTextDialog("Der PaziFist", str));
			this.appendMessage(str);
		}
		if (lvl == 5){
			canUseMeditate = true;
			String str = "(Lvl 5) Ich kann nun meditieren. Zur Beruhigung und Stärkung meines Glaubens. ('2')";
			world().setActiveDialog(Dialog.createSimpleTextDialog("Der PaziFist", str));
			this.appendMessage(str);
		}
		if (lvl == 7){
			canUseRedemption = true;
			String str = "(Lvl 7) Mein Glaube ist nun so gefestigt, dass ich arme Seelen retten kann! ('3')";
			world().setActiveDialog(Dialog.createSimpleTextDialog("Der PaziFist", str));
			this.appendMessage(str);
		}
		if (lvl == 9){
			canUseRoundhousePunch = true;
			String str = "(Lvl 9) AAAAAHHHHHHHH! ROUNDHOUSEPUNCH freigeschaltet (bei mind. 80% Rage: '4')";
			world().setActiveDialog(Dialog.createSimpleTextDialog("Der PaziFist", str));
			this.appendMessage(str);
		}
		
	}

	public void showHelp() {
		ArrayList<String> helpList = new ArrayList<String>();
		helpList.add("Bewegen mit Pfeiltasten");
		helpList.add("Angreifen oder reden mit Gegenlaufen");
		helpList.add("Heiltrank benutzen: 'H'");
		helpList.add("Fernkampfwaffe: Leertaste + Richtung");
		helpList.add("Waffe wechseln: Nahkampf 'Q' , Fernkampf 'W'");
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

	public void changeMelee() {
		if (inventory.getItems(MeleeWeaponPrototype.class) != null)
			this.useItem(inventory.getItems(MeleeWeaponPrototype.class));
	}
	
	public void refreshWeaponsHUD(){
		HUD.setWeaponLbl(meleeWeapon, rcWeapon);
	}
	
	public void changeRC() {
		if (inventory.getItems(RCWeaponPrototype.class) != null)
			this.useItem(inventory.getItems(RCWeaponPrototype.class));
	}

	public void addRage(int rage) {
		this.rage += rage;
		if(this.rage > 100)
			this.rage = 100;
	}

	public void addFaith(int faith) {
		this.faith += faith;
		if(this.faith > 100)
			this.faith = 100;
	}
}
