package rogue.level;

import jade.core.World;
import jade.gen.Generator;
import jade.gen.map.City;
import rogue.creature.Player;

public class Level extends World
{
    private final static Generator gen = getLevelGenerator();

    public Level(int width, int height, Player player, String Name)
    {
        super(width, height, Name);
        gen.generate(this);
        addActor(player);
    }

    private static Generator getLevelGenerator()
    {
    	return new City();
//        return new Cellular();
    }
}
