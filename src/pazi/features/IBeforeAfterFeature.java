package pazi.features;

import jade.core.Actor;

public interface IBeforeAfterFeature<T extends Actor> {
	public void actBefore(T actor);
	public void actAfter(T actor);
}
