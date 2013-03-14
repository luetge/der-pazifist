package jade.util.datatype;

import jade.util.Guard;

import java.awt.event.KeyEvent;

/**
 * A cardinal direction as a 2-dimensional integer vector.
 */
public enum Direction
{
    /**
     * Up on the screen
     */
    NORTH(0, -1),
    /**
     * Right on the screen
     */
    EAST(1, 0),
    /**
     * Down on the screen
     */
    SOUTH(0, 1),
    /**
     * Left on the screen
     */
    WEST(-1, 0),
    NORTHWEST(-1, -1),
    NORTHEAST(1, -1),
    SOUTHWEST(-1, 1),
    SOUTHEAST(1, 1),
    /**
     * No change on the screen
     */
    ORIGIN(0, 0);

    private int dx;
    private int dy;

    private Direction(int dx, int dy)
    {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Returns the x component of the directional vector.
     * @return the x component of the directional vector
     */
    public int dx()
    {
        return dx;
    }

    /**
     * Returns the y component of the directional vector.
     * @return the y component of the directional vector
     */
    public int dy()
    {
        return dy;
    }
    
    public int getID()
    {
    	return (dy+1)*3+dx+1;
    }

    /**
     * Returns the {@code Direction} corresponding to the given key press, or null if there is none.
     * The key can be either vi-keys (with '.' as {@code ORIGIN}), or num-pad keys.
     * @param key the key direction being queried
     * @return the {@code Direction} corresponding to key
     */
    public static Direction keyToDir(int key)
    {
        switch(key)
        {
        	case KeyEvent.VK_6:
        	case KeyEvent.VK_RIGHT:
                return EAST;
        	case KeyEvent.VK_4:
        	case KeyEvent.VK_LEFT:
                return WEST;
        	case KeyEvent.VK_8:
        	case KeyEvent.VK_UP:
                return NORTH;
        	case KeyEvent.VK_2:
        	case KeyEvent.VK_DOWN:
                return SOUTH;
            case '5':
            case '.':
                return ORIGIN;
            default:
                return null;
        }
    }
}
