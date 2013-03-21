package pazi.behaviour;

import org.lwjgl.input.Keyboard;

import pazi.weapons.SniperRifle;

import jade.util.datatype.Direction;
import rogue.creature.Creature;
import rogue.creature.Monster;
import rogue.creature.Player;
import rogue.creature.Creature.AttackableCreature;

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
