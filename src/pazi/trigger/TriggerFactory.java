package pazi.trigger;

import jade.core.World;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;
import rogue.creature.Creature;
import rogue.creature.CreatureFactory;
import rogue.creature.Player;

public class TriggerFactory {
	public static ITrigger createTrigger(String identifier, World world) {
		ITrigger trigger = null;
		if(identifier.equals("zombieguard")) {
			CreatureTrigger ct = new CreatureTrigger(null, 3, Player.class);
			ct.onEnterEvent = new ICreatureEvent() {
				@Override
				public void fired(Creature creature, CreatureTrigger trigger) {
					Coordinate temp;
					for(Direction dir : Direction.values()) {
						temp = trigger.pos().getTranslated(dir);
						if(creature.world().insideBounds(temp) && creature.world().passableAt(temp) && creature.world().getActorAt(Creature.class, temp) == null)
							creature.world().addActor(CreatureFactory.createCreature("zombie1", creature.world()), temp);
					}
					creature.world().setMessage("Harharhar, du wirst niemals an mich herankommen!");
					creature.world().getTrigger().remove(trigger);
				}
			};
			trigger = ct;
		}
		return trigger;
	}
}
