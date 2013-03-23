package pazi.gen.feature;

import pazi.gen.Generator;
import pazi.util.Guard;

/**
 * Represents an algorithm for adding features to a pre-existing map. Since the {@code
 * FeatureGenerator} is meant to embellish a map rather than generate the whole map, it requires
 * that it be chained with a {@code Generator} that already does the map generation.
 */
public abstract class FeatureGenerator extends Generator
{
    /**
     * Creates a new instance of {@code FeatureGenerator} with the required chained {@code
     * Generator}.
     * @param chained the required chained {@code Generator}
     */
    public FeatureGenerator(Generator chained)
    {
        super(chained);
        Guard.argumentIsNotNull(chained);
    }
}
