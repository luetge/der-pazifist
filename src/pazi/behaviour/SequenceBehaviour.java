package pazi.behaviour;

import jade.core.World;
import jade.util.datatype.Coordinate;

import java.util.Iterator;
import java.util.LinkedList;

import pazi.trigger.CreatureTrigger;
import pazi.trigger.ICreatureEvent;

import rogue.creature.Creature;
import rogue.creature.Player;

public class SequenceBehaviour implements IBehaviour<Creature> {

	LinkedList<Coordinate> sequence = new LinkedList<Coordinate>();
	Iterator<Coordinate> iter;
	IBehaviour nextBehaviour;
	boolean play = false;
	World world;
	int radius;
	
	/**
	 * Eingabe: (x1, y1)->(x2, y2)->...->(xn, yn)
	 * @param sequence
	 */
	public SequenceBehaviour(String sequence, IBehaviour nextBehaviour, World world, int radius) {
		//Leerzeichen entfernen
		sequence = sequence.replace(" ", "");
		for(String s : sequence.split("->"))
			addCoordinateToList(s, this.sequence);
		iter = this.sequence.iterator();
		this.nextBehaviour = nextBehaviour;
		this.world = world;
		this.radius = radius;
	}
	
	public SequenceBehaviour(String sequence, World world, int radius) {
		this(sequence, new DefaultBehaviour(), world, radius);
	}
	
	private void addCoordinateToList(String s, LinkedList<Coordinate> lst) {
		try {
		if(s.isEmpty())
			return;
		String[] split = s.substring(1, s.length()-1).split(",");
		Coordinate newCoord = new Coordinate(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
		if(newCoord != null)
			lst.add(newCoord);
		} catch (Exception ex) { }
	}

	@Override
	public void act(Creature actor) {
		if(!play)
			return;
		
		if(iter.hasNext())
			actor.setPos(iter.next());
		else
			actor.setBehaviour(nextBehaviour);
	}

	@Override
	public void init(Creature actor) {
		act(actor);
		if(radius == -1){
			play = true;
			return;
		}
		CreatureTrigger ct = new CreatureTrigger(null, radius, Player.class);
		ct.onEnterEvent = new ICreatureEvent() {
			public void fired(Creature creature, CreatureTrigger trigger) {
				play = true;
			}
		};
		world.getTrigger().add(ct);
	}

	@Override
	public void exit(Creature actor) { }

}
