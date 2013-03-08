package rogue.creature;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;

public abstract class Creature extends Actor
{
    public Creature(ColoredChar face, String Name)
    {
        super(face, Name);
    }

    @Override
    public void setPos(int x, int y)
    {
        if(world().passableAt(x, y))
            super.setPos(x, y);
    }
    
    public abstract void walk();
    
    @Override
    public void appendMessage(String message) {
    	world().appendMessage(message, this);
    }
}
