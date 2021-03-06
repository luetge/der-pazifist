package pazi.fov;

import java.util.Collection;
import java.util.HashSet;

import pazi.core.World;
import pazi.path.Bresenham;
import pazi.util.datatype.Coordinate;

/**
 * Uses a simple raycasting algorithm to compute field of view. This algorithm works by sending
 * digital line rays to each point on the outside of the view radius. This algorithm is very simple,
 * and therefore can often be very fast, especially if the radius is small, or the map is very
 * closed. However, on large and open maps the algorithm results in inefficiencies as tiles close
 * the origin of the view field are rechecked many times over.
 */
public class RayCaster extends ViewField
{
    private Bresenham raycaster;

    /**
     * Constructs a new {@code RayCaster}.
     */
    public RayCaster()
    {
        raycaster = new Bresenham();
    }

    @Override
    protected Collection<Coordinate> calcViewField(World world, int x, int y, int r)
    {
        Collection<Coordinate> fov = new HashSet<Coordinate>();
        fov.add(new Coordinate(x, y));
        int ry = r * 3/4;

        for(int dx = -r; dx <= r; dx++)
        {
            fov.addAll(raycaster.getPartialPath(world, x, y, x + dx, y - ry));
            fov.addAll(raycaster.getPartialPath(world, x, y, x + dx, y + ry));
        }
        for(int dy = -ry; dy <= ry; dy++)
        {
            fov.addAll(raycaster.getPartialPath(world, x, y, x - r, y + dy));
            fov.addAll(raycaster.getPartialPath(world, x, y, x + r, y + dy));
        }

        return fov;
    }
}
