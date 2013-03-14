package pazi.behaviour;

import jade.core.Actor;

public interface IBehaviour<T extends Actor> {
	public void act(T actor);
	public void init(T actor);
	public void exit(T actor);
}