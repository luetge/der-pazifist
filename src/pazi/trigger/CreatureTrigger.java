package pazi.trigger;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import pazi.core.Actor;
import pazi.core.World;
import pazi.creature.Creature;
import pazi.util.datatype.ColoredChar;
import pazi.util.datatype.Coordinate;


public class CreatureTrigger extends Actor implements ITrigger {

	protected boolean activated = true;
	protected double range;
	protected Class cls;
	public ICreatureEvent onEnterEvent, onExitEvent, movingInsideEvent;
	protected ArrayList<Creature> creaturesInRange = new ArrayList<Creature>();
	
	public <T extends Creature> CreatureTrigger(Coordinate pos, double range, Class<T> cls) {
		super(new ColoredChar(' ', Color.black), "Trigger");
		if(pos != null)
			this.setPos(pos);
		this.setRange(range);
		this.setCls(cls);
	}
	
	@Override
	public void tick(World world) {
		if(!activated)
			return;
		
		// Alle Kreaturen in der Nähe betrachten
		List<Coordinate> coords = Creature.getRect(pos(), getRange());
		for(Coordinate coord : coords){
			if(!world.insideBounds(coord))
				continue;
			for(Object creat : world.getActorsAt(getCls(), coord)){
				if(creaturesInRange.contains(creat)){
					if(movingInsideEvent != null)
						movingInsideEvent.fired((Creature) creat, this);
					continue;
				} else if(onEnterEvent != null)
					onEnterEvent.fired((Creature)creat, this);
				creaturesInRange.add((Creature)creat);
			}
		}
		
		// Kreaturen entfernen, wenn sie zu weit weg sind
		for(int i = creaturesInRange.size(); i > 0; i--) 
			if(creaturesInRange.get(i - 1).pos().maxDist(pos()) > getRange()){
				if(onExitEvent != null)
					onExitEvent.fired(creaturesInRange.get(i - 1), this);
				creaturesInRange.remove(i - 1);
			}
	}
		
	@Override
	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@Override
	public boolean activated() {
		return activated;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public Class getCls() {
		return cls;
	}

	public void setCls(Class cls) {
		this.cls = cls;
	}

	@Override
	public void interact(Actor actor) {}
	
	@Override
	public Coordinate pos() {
		if(holder() != null)
			return holder().pos();
		return super.pos();
	}

}
