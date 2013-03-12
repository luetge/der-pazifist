package rogue.creature;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;
import pazi.features.Death;

public abstract class Creature extends Actor
{
	protected boolean neutralized = false;
	protected int hp = 100;
	protected int min_d, max_d;
	
    public Creature(ColoredChar face, String Name)
    {
        super(face, Name);
    }

    @Override
    public void setPos(int x, int y)
    {
    	Actor actor = world().getActorAt(Actor.class, x, y);
        if(world().passableAt(x, y))
        	if (actor == null || actor.isPassable())
        		super.setPos(x, y);
        	else
        		interact(world().getActorAt(Creature.class, x, y));
        	
    }
    
    public abstract void walk();
    
    @Override
    public void appendMessage(String message) {
    	world().appendMessage(message, this);
    }
    
    public void neutralize(){
    	neutralized = true;
    	setPassable(true);
    }
    
    public boolean isNeutralized(){
    	return neutralized;
    }
    
    @Override
    public void interact(Actor actor) {
    	if(Creature.class.isAssignableFrom(actor.getClass()))
    		this.fight((Creature)actor);
    }
    
    public void fight(Creature creature){
    	if(getClass() == creature.getClass())
    		return;
    	creature.takeDamage((int)Math.floor(Math.random()* (max_d - min_d) + min_d));
    }
    
    public void takeDamage(int d){
    	hp = Math.max(0, hp-d);
    	appendMessage("Ich habe " + d + " Schaden erlitten! Ahhhh Poopoo!");
    	if(hp == 0)
    		addFeature(new Death(this));
    }
}
