package jade.util.datatype;

import org.lwjgl.input.Keyboard;

/**
 * A cardinal direction as a 2-dimensional integer vector.
 */
public enum Direction
{
    /**
     * Up on the screen
     */
    NORTH(0, -1), // ID: 3
    /**
     * Right on the screen
     */
    EAST(1, 0), // ID: 7
    /**
     * Down on the screen
     */
    SOUTH(0, 1), // ID: 5
    /**
     * Left on the screen
     */
    WEST(-1, 0), // ID: 1
    NORTHWEST(-1, -1), // ID: 0
    NORTHEAST(1, -1), // ID: 6
    SOUTHWEST(-1, 1), // ID: 2
    SOUTHEAST(1, 1), // ID: 8
    /**
     * No change on the screen
     */
    ORIGIN(0, 0); // ID: 4

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
    	return (dx+1)*3+dy+1;
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
        	case Keyboard.KEY_NUMPAD6:
        	case Keyboard.KEY_RIGHT:
        		return EAST;
        	case Keyboard.KEY_NUMPAD4:
        	case Keyboard.KEY_LEFT:
        		return WEST;
        	case Keyboard.KEY_NUMPAD8:
        	case Keyboard.KEY_UP:
                return NORTH;
        	case Keyboard.KEY_NUMPAD2:
        	case Keyboard.KEY_DOWN:
                return SOUTH;
            case Keyboard.KEY_NUMPAD5:
            case Keyboard.KEY_COLON:
                return ORIGIN;
            default:
                return null;
        }
    }
}
