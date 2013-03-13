package pazi.features;

import jade.core.Actor;

public interface IFeature<T extends Actor> {
	public void act(T actor);
}
