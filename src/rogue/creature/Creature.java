package rogue.creature;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;

public abstract class Creature extends Actor
{
	protected boolean neutralized = false;
	
    public Creature(ColoredChar face, String Name)
    {
        super(face, Name);
    }

    @Override
    public void setPos(int x, int y)
    {
        if(world().passableAt(x, y))
        	if (world().getActorAt(Creature.class, x, y)== null)
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
    }
    
    public boolean isNeutralized(){
    	return neutralized;
    }
}
