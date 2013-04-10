package pazi.features;

import pazi.core.Actor;

public interface IFeature<T extends Actor> {
	public void act(T actor);
}
