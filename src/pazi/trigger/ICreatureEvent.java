package pazi.trigger;

import pazi.creature.Creature;

public interface ICreatureEvent {
	void fired(Creature creature, CreatureTrigger trigger);
}
