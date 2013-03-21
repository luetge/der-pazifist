package rogue.creature;

import jade.core.Actor;
import jade.core.Messenger;
import jade.util.Guard;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import pazi.behaviour.DeadBehaviour;
import pazi.behaviour.DoNothingBehaviour;
import pazi.behaviour.IBehaviour;
import pazi.features.IBeforeAfterFeature;
import pazi.items.Inventory;
import pazi.items.Item;
import pazi.items.ItemFactory;
import pazi.weapons.IMeleeWeapon;
import pazi.weapons.IRangedCombatWeapon;
import pazi.weapons.IWeapon;
import pazi.weapons.WeaponPrototype;

public abstract class Creature extends Actor
{
	protected int hp = 100;
	protected int maxHp = 100;
	protected int min_d, max_d;
	protected int givenXp = 25;
    protected int xp=0;
    protected int lvl=1;
	protected Coordinate nextCoordinate;
	protected LinkedList<IBeforeAfterFeature> walkFeatures = new LinkedList<IBeforeAfterFeature>();
	protected IBehaviour walkBehaviour;
    protected LinkedList<IBeforeAfterFeature> fightFeatures = new LinkedList<IBeforeAfterFeature>();
    protected Inventory inventory = new Inventory(this);
    protected IMeleeWeapon meleeWeapon;
    protected IRangedCombatWeapon rcWeapon;
    
    private ColoredChar faces[];
    
    public Creature(ColoredChar face, String Name)
    {
        this(new ColoredChar[]{face, face, face, face, face, face, face, face ,face}, Name);
    }
    
    public Creature(ColoredChar faces[], String Name)
    {
    	super (faces[4], Name);
    	Guard.validateArgument(faces.length == 9);
    	this.faces = faces;

        walkBehaviour = DoNothingBehaviour.getInstance();
        setBehaviour(DoNothingBehaviour.getInstance());
        meleeWeapon = (IMeleeWeapon) ItemFactory.createWeapon("headnut", this);
    }
    
    public void setFace (Direction dir, ColoredChar face)
    {
    	this.faces[dir.getID()] = face;
    }
    
    public void setAllFaces (ColoredChar face)
    {
    	for (int i = 0; i < 9; i++)
    		this.faces[i] = face;
    }
    
    public void setFaces (ColoredChar faces[])
    {
    	Guard.validateArgument(faces.length == 9);
    	this.faces = faces;
    }
    
    public void setCurrentFace (int id)
    {
    	Guard.argumentInsideBound(id, 9);
    	setFace (faces[id]);
    }

    @Override
    public void setPos(int x, int y)
    {
	    if (world().insideBounds(x, y)){
	    	Collection<Actor> actors = world().getActorsAt(Actor.class, x, y);
	        if(world().passableAt(x, y)){
	        	for(Actor actor : actors)
	        		if(!actor.isPassable())
	        			return;
	    		super.setPos(x, y);
	        }
	    }
    }

    @Override
    public void appendMessage(String message, Messenger source,	boolean important) {
    	world().appendMessage(message, source, important);
    }
    
    @Override
    public void interact(Actor actor) {
    	Guard.verifyState(isPlayer());
    	if(Creature.class.isAssignableFrom(actor.getClass()))
    		this.fight((Creature)actor, true);
    	/*else if (Ally.class.isAssignableFrom(actor.getClass()))
    		this.talk((Ally)actor);*/
    }
    
    public void interact (Direction dir) {
    	if (dir == null || dir == Direction.ORIGIN)
    		return;
    	Guard.verifyState(isPlayer());
    	Collection<Creature> creatures = world().getActorsAt(Creature.class, pos().getTranslated(dir));
    	for (Creature creature : creatures)
    		if(!Ally.class.isAssignableFrom(creature.getClass()))
    			fight(creature, true);
    	Collection<Ally> allies = world().getActorsAt(Ally.class, pos().getTranslated(dir));
    	for (Ally ally : allies)
    		talkto(ally);
    }
    
    public void talkto (Ally ally)
    {
    	
    }
    
    public void fight(Creature creature, boolean melee){
    	IWeapon weapon = melee ? meleeWeapon : rcWeapon;
    	if(weapon == null)
    		return;
    	fight(creature, weapon.getDamage(this, creature), weapon.getProb(this, creature), melee);
    }    
    
    public void takeDamage(int d,Creature source){
    	if(getBehaviour().getClass() == DeadBehaviour.class)
    		return;
    	setHP(Math.max(0, hp-d));
    	appendMessage("Ich habe " + d + " Schaden erlitten! Ahhhh Poopoo!", true);
    	if(hp == 0)
    		setBehaviour(new DeadBehaviour(this,source));
    }
    
    public void addHP(int hp){
    	setHP(this.hp + hp);
    }
    
	public void doStep() {
		if(nextCoordinate != null)
		{
			Direction dir = pos().directionTo(nextCoordinate);
			setCurrentFace (dir.getID());
			setPos(nextCoordinate);
		}
	}
	
	public void setNextCoord(Coordinate coordinate) {
		nextCoordinate = coordinate;
	}
    
    public void fight(Creature creature, int hp, double chance, boolean melee) {
    	if (creature != null && creature.isPassable())
    		return;
     	if(hasActed())
    		return;
    	// FIGHT!!!
		for(IBeforeAfterFeature<Creature> feature : fightFeatures)
			feature.actBefore(this);
		(melee ? meleeWeapon : rcWeapon).shoot(this, creature);
		setHasActed(true);
		for(IBeforeAfterFeature<Creature> feature : fightFeatures)
			feature.actAfter(this);
	}

	public void walk() {
		if(hasActed() || walkBehaviour == null)
			return;
		// Neue Position bestimmen
		for(IBeforeAfterFeature<Creature> feature : walkFeatures)
			feature.actBefore(this);
		walkBehaviour.act(this);
		for(IBeforeAfterFeature<Creature> feature : walkFeatures)
			feature.actAfter(this);
		// Creature auf Position verschieben
		doStep();
	}
	
	public void setWalkBehaviour(IBehaviour walkBehaviour){
		if(this.walkBehaviour != null)
			this.walkBehaviour.exit(this);
		this.walkBehaviour = walkBehaviour;
		if(walkBehaviour != null)
			walkBehaviour.init(this);
	}
	
	public IBehaviour getWalkBehaviour(){
		return walkBehaviour;
	}
	
	public LinkedList<IBeforeAfterFeature> getWalkFeatures(){
		return walkFeatures;
	}
	
	public LinkedList<IBeforeAfterFeature> getFightFeatures(){
		return fightFeatures;
	}
	
	protected void gainHP(int raise){
		setHP(hp + raise);
	}
	
	protected void setHP(int hp){
		this.hp = hp;
		if(this.hp > this.maxHp)
			this.hp = maxHp;
		if(this.hp < 0)
			this.hp = 0;
	}
	
	public int getHP(){
		return hp;
	}
	
	public Inventory getInventory() {
		return inventory;
	}

	public void dropInventory() {
		if(inventory.getGold() > 0)
			world().addActor(ItemFactory.createGold(inventory.getGold()), pos());
	}
	
	public int getGold(int amount) {
		if(amount >= 0) {
			inventory.findGold(amount);
			return amount;
		} else
			return inventory.loseGold(-amount);
	}
	
	public void useItem(Item item){
		setHasActed(true);
		item.interact(this);
		getInventory().removeItem(item);
	}
	
	public AttackableCreature getAttackableCreature(Class cls){
		ArrayList<AttackableCreature> lst = getCreatures();
		Collections.sort(lst, new Comparator<AttackableCreature>() {
			public int compare(AttackableCreature o1, AttackableCreature o2) {
				return (int)(-(o1.damage*o1.prob - o2.damage*o2.prob));
			};
		});
		for(AttackableCreature creat : lst)
			if(cls.isAssignableFrom(creat.creature.getClass()))
					return creat;
		return null;
	}
	
	
	public ArrayList<Creature> getCreaturesCloseby(){
		ArrayList<Creature> list = new ArrayList<Creature>();

		list.addAll(world().getActorsAt(Creature.class, pos().getTranslated(1, -1)));
		list.addAll(world().getActorsAt(Creature.class, pos().getTranslated(1, 0)));
		list.addAll(world().getActorsAt(Creature.class, pos().getTranslated(1, 1)));
		list.addAll(world().getActorsAt(Creature.class, pos().getTranslated(0, -1)));
		list.addAll(world().getActorsAt(Creature.class, pos().getTranslated(0, 1)));
		list.addAll(world().getActorsAt(Creature.class, pos().getTranslated(-1, -1)));
		list.addAll(world().getActorsAt(Creature.class, pos().getTranslated(-1, 0)));
		list.addAll(world().getActorsAt(Creature.class, pos().getTranslated(-1, 1)));
	
		list.removeAll(Collections.singletonList(null));
		return list;
	}
	
	public ArrayList<AttackableCreature> getCreatures(){
		ArrayList<AttackableCreature> list = new ArrayList<AttackableCreature>();
		addAttackableCreature(list, getScore(pos().getTranslated(-1, 0), true));
		addAttackableCreature(list, getScore(pos().getTranslated(1, 0), true));
		addAttackableCreature(list, getScore(pos().getTranslated(0, 1), true));
		addAttackableCreature(list, getScore(pos().getTranslated(0, -1), true));
		if(rcWeapon != null)
			for(Coordinate coord : getRect(pos(), rcWeapon.getRange()))
				addAttackableCreature(list, getScore(coord, false));
		return list;
	}
	
	public static List<Coordinate> getRect(Coordinate pos, double range) {
		ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
		int half = (int)(range/2);
		for(int i=0; i<2*range; i++)
			for(int j=0; j<2*range; j++)
				coords.add(pos.getTranslated(i-half, j-half));
		return coords;
	}

	protected void addAttackableCreature(ArrayList<AttackableCreature> list, AttackableCreature creature){
		if(creature != null)
			list.add(creature);
	}
	
	protected AttackableCreature getScore(Coordinate coord, boolean melee){
		if(!world().insideBounds(coord))
			return null;
		IWeapon weapon = melee ? meleeWeapon : rcWeapon;
		for(Creature creature : world().getActorsAt(Creature.class, coord))
			if(!creature.expired())
				return new AttackableCreature(creature, weapon.getDamage(this, creature), weapon.getProb(this, creature), melee);
		return null;
	}
	
	public class AttackableCreature {
		public final Creature creature;
		public final int damage;
		public final double prob;
		public final boolean melee;
		
		public AttackableCreature(Creature creature, int damage, double prob, boolean melee) {
			this.creature = creature;
			this.damage = damage;
			this.prob = prob;
			this.melee = melee;
		}
	}

	public void setMeleeWeapon(IMeleeWeapon weapon) {
		if (weapon == null)
			meleeWeapon = (IMeleeWeapon) ItemFactory.createWeapon("headnut");
		else{
			inventory.addItem((Item)meleeWeapon);
			meleeWeapon = weapon;
		}
	}
	
	public void setRCWeapon(IRangedCombatWeapon weapon) {
		inventory.addItem((Item)rcWeapon);
		rcWeapon = weapon;
	}
	
	public int getXp(){
		return this.givenXp;
	}
	
	public void gainXp(int xp){		
	}

	public void levelUp(){
	}

	public void setWeapon(WeaponPrototype weapon) {
		if(IMeleeWeapon.class.isAssignableFrom(weapon.getClass()))
			setMeleeWeapon((IMeleeWeapon) weapon);
		else
			setRCWeapon((IRangedCombatWeapon) weapon);
		weapon.setHolder(this);
	}

	public void killedSomeone(Creature creature) {}

	public void expireWeapon(WeaponPrototype weapon) {
		if (IMeleeWeapon.class.isAssignableFrom(weapon.getClass())){
			setMeleeWeapon(null);
			this.appendMessage(weapon.getName() + " ist zerbrochen. Shit!");
		} else{
			setRCWeapon(null);
			this.appendMessage(weapon.getName() + " hat keine Muni mehr. Weg damit!");
		}
	}

	public String getDeathMessage() {
		return "UUuuuuuuaaaaarrrrrrrghghhgghhh!";
	}
}
