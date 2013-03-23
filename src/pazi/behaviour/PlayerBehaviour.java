package pazi.behaviour;

import org.lwjgl.input.Keyboard;

import pazi.creature.Creature;
import pazi.creature.Monster;
import pazi.creature.Player;
import pazi.creature.Creature.AttackableCreature;
import pazi.util.datatype.Direction;
import pazi.weapons.SniperRifle;


public class PlayerBehaviour implements IBehaviour<Creature> {
	@Override
	public void act(Creature creature) {
		if(creature.hasActed())
			return;
		
		if(creature.world().getCurrentKey() == Keyboard.KEY_SPACE){
			if (creature.getRCWeapon() == null)
				return;			
			if (creature.getRCWeapon().getClass() == SniperRifle.class){
				
				AttackableCreature creat = creature.getAttackableCreature(Monster.class);
				if(creat == null)
					return;
				creature.fight(creat.creature, false);

			} else
				creature.fight(null, false);
		}
		Direction dir = Direction.keyToDir(creature.world().getCurrentKey());
		creature.interact(dir);
		creature.walk();
	}

	@Override
	public void exit(Creature actor) {}

	@Override
	public void init(Creature actor) {}
}
