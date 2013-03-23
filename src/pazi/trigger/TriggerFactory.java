package pazi.trigger;

import pazi.core.Dialog;
import pazi.core.World;
import pazi.creature.Ally;
import pazi.creature.Creature;
import pazi.creature.CreatureFactory;
import pazi.creature.JokesTeller;
import pazi.creature.Player;
import pazi.util.datatype.Coordinate;
import pazi.util.datatype.Direction;

public class TriggerFactory {
	public static ITrigger createTrigger(String identifier, World world) {
		ITrigger trigger = null;
		if(identifier.equals("zombieguard")) {
			CreatureTrigger ct = new CreatureTrigger(null, 4, Player.class);
			ct.movingInsideEvent = new ICreatureEvent() {
				@Override
				public void fired(Creature creature, CreatureTrigger trigger) {
					Coordinate temp;
					for(Direction dir : Direction.values()) {
						temp = trigger.pos().getTranslated(dir);
						if(creature.world().insideBounds(temp) && creature.world().passableAt(temp) && creature.world().getActorAt(Creature.class, temp) == null)
							creature.world().addActor(CreatureFactory.createCreature("nazi", creature.world()), temp); //Hitler spawns Nazis
					}
					creature.world().setMessage("Harharhar, du wirst niemals an mich herankommen!");
					trigger.setActivated(false);
				}
			};
			trigger = ct;
		} else if (identifier.equals("tut3diag")) {
			CreatureTrigger ct = new CreatureTrigger (null, 0, Ally.class);
			ct.setPassable(true);
			ct.onEnterEvent = new ICreatureEvent () {
				@Override
				public void fired(Creature creature, CreatureTrigger trigger) {
					creature.world().setActiveDialog(new Dialog("res/dialogs/tut3diag.txt"));
					trigger.setActivated(false);
				}
			};
			trigger = ct;
		} else if (identifier.equals("jokestrigger")) {
			CreatureTrigger ct = new CreatureTrigger(null, 2, Player.class);
			ct.onEnterEvent = new ICreatureEvent() {
				@Override
				public void fired(Creature creature, CreatureTrigger trigger) {
					creature.appendMessage(JokesTeller.getRandomMessage());
				}
			};
			trigger = ct;
		}
		return trigger;
	}
}
