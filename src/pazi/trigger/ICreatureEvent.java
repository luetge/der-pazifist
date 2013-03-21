package pazi.trigger;

import rogue.creature.Creature;

public interface ICreatureEvent {
	void fired(Creature creature, CreatureTrigger trigger);
}
