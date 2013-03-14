package pazi.behaviour;

import jade.core.Actor;

public class DoNothingBehaviour implements IBehaviour<Actor> {

	/**
	 * Singleton
	 */
	private static DoNothingBehaviour inst;
	
	private DoNothingBehaviour(){}
	
	public static DoNothingBehaviour getInstance(){
		if(inst == null)
			inst = new DoNothingBehaviour();
		return inst;
	}
	
	@Override
	public void act(Actor actor) {}

	@Override
	public void exit(Actor actor) {}

	@Override
	public void init(Actor actor) {}

}
