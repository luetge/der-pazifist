package rogue.creature;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

import java.util.Collection;
import java.util.LinkedList;

import pazi.features.DeadBehaviour;
import pazi.features.Death;
import pazi.features.DoNothing;
import pazi.features.IBeforeAfterFeature;
import pazi.features.IFeature;

public abstract class Creature extends Actor
{
	protected int hp = 100;
	protected int min_d, max_d;
	protected Coordinate nextCoordinate;
	protected LinkedList<IBeforeAfterFeature> walkFeatures = new LinkedList<IBeforeAfterFeature>();
	protected IFeature walkBehaviour;
    protected LinkedList<IBeforeAfterFeature> fightFeatures = new LinkedList<IBeforeAfterFeature>();
    protected IFeature fightBehaviour;
    
    public Creature(ColoredChar face, String Name)
    {
        super(face, Name);
        walkBehaviour = new DoNothing();
        fightBehaviour = new DoNothing();
        setBehaviour(new DoNothing());
    }

    @Override
    public void setPos(int x, int y)
    {
    	Collection<Actor> actors = world().getActorsAt(Actor.class, x, y);
        if(world().passableAt(x, y)){
        	for(Actor actor : actors)
        		if(!actor.isPassable())
        			return;
    		super.setPos(x, y);
    		setHasActed(true);
        }
    }
    
    @Override
    public void appendMessage(String message) {
    	world().appendMessage(message, this);
    }
    
    @Override
    public void interact(Actor actor) {
    	if(Creature.class.isAssignableFrom(actor.getClass()))
    		this.fight((Creature)actor);
    }
    
    public void fight(Creature creature){
    	if(creature == null || getClass() == creature.getClass())
    		return;
    	creature.takeDamage((int)Math.floor(Math.random()* (max_d - min_d) + min_d));
    }
    
    public void takeDamage(int d){
    	hp = Math.max(0, hp-d);
    	appendMessage("Ich habe " + d + " Schaden erlitten! Ahhhh Poopoo!");
    	if(hp == 0)
    		setBehaviour(new DeadBehaviour(this));
    }

	public void doStep() {
		if(nextCoordinate != null)
			setPos(nextCoordinate);		
	}
	
	public void setNextCoord(Coordinate coordinate) {
		nextCoordinate = coordinate;
	}
    
    public void fight() {
    	// FIGHT!!!
		for(IBeforeAfterFeature<Creature> feature : fightFeatures)
			feature.actBefore(this);
		fightBehaviour.act(this);
		for(IBeforeAfterFeature<Creature> feature : fightFeatures)
			feature.actAfter(this);
	}

	public void walk() {
		// Neue Position bestimmen
		for(IBeforeAfterFeature<Creature> feature : walkFeatures)
			feature.actBefore(this);
		walkBehaviour.act(this);
		for(IBeforeAfterFeature<Creature> feature : walkFeatures)
			feature.actAfter(this);
		// Creature auf Position verschieben
		doStep();
	}
	
	public void setWalkBehaviour(IFeature walkBehaviour){
		this.walkBehaviour = walkBehaviour;
	}
	
	public IFeature getWalkBehaviour(){
		return walkBehaviour;
	}
	
	public void setFightBehaviour(IFeature fightBehaviour){
		this.fightBehaviour = fightBehaviour;
	}
	
	public IFeature getFightBehaviour(){
		return fightBehaviour;
	}
	
	public LinkedList<IBeforeAfterFeature> getWalkFeatures(){
		return walkFeatures;
	}
	
	public LinkedList<IBeforeAfterFeature> getFightFeatures(){
		return fightFeatures;
	}

	public void fight(Direction dir) {
		fight(world().getActorAt(Creature.class, pos().getTranslated(dir)));
	}
}
