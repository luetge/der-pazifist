package pazi.trigger;

import jade.core.World;
import jade.util.datatype.Direction;
import rogue.creature.Creature;
import rogue.creature.CreatureFactory;
import rogue.creature.Player;

public class TriggerFactory {
	public static ITrigger createTrigger(String identifier, World world) {
		ITrigger trigger = null;
		if(identifier.equals("zombieguard")) {
			CreatureTrigger ct = new CreatureTrigger(null, 4, Player.class);
			ct.onEnterEvent = new ICreatureEvent() {
				@Override
				public void fired(Creature creature, CreatureTrigger trigger) {
					for(Direction dir : Direction.values())
						if(creature.world().getActorAt(Creature.class, trigger.pos().getTranslated(dir)) == null)
							creature.world().addActor(CreatureFactory.createCreature("zombie1", creature.world()), trigger.pos().getTranslated(dir));
					creature.world().setMessage("Harharhar, du wirst niemals an mich herankommen!");
					creature.world().getTrigger().remove(trigger);
				}
			};
			trigger = ct;
		}
		return trigger;
	}
}
