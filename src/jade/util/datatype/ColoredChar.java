package jade.util.datatype;

import jade.util.Guard;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * An immutable tuple of ({@code char}, {@code Color}). This is useful for storing screen display
 * information.
 */
public class ColoredChar
{
    private final char ch;
    private final Color color;
    private final Color bgcolor;

    private static Map<ColoredChar, ColoredChar> interned = new HashMap<ColoredChar, ColoredChar>();

    /**
     * Gets the {@code ColoredChar} tuple with the given values. This will always return the
     * interned canonical version of the particular {@code ColoredChar}.
     * @param ch the {@code char} value of the {@code ColoredChar}
     * @param color the {@code Color} value of the {@code ColoredChar}
     * @return the interned {@code ColoredChar} with the specified values
     */
    public static ColoredChar create(char ch, Color color)
    {
        ColoredChar instance = new ColoredChar(ch, color);
        if(!interned.containsKey(instance))
            interned.put(instance, instance);
        return interned.get(instance);
    }

    public static ColoredChar create(char ch, Color color, Color bgcolor)
    {
        ColoredChar instance = new ColoredChar(ch, color, bgcolor);
        if(!interned.containsKey(instance))
            interned.put(instance, instance);
        return interned.get(instance);
    }

    /**
     * Constructs a new {@code ColoredChar} tuple with the given {@code char} value and {@code
     * Color.white} as the default {@code Color} value. This will always return the interned
     * canonical version of the particular {@code ColoredChar}.
     * @param ch the {@code char} value of the {@code ColoredChar}
     * @return the interned {@code ColoredChar} with the specified values
     */
    public static ColoredChar create(char ch)
    {
        return create(ch, Color.white);
    }

    public ColoredChar(char ch, Color color, Color bgcolor)
    {
        Guard.argumentIsNotNull(color);

        this.ch = ch;
        this.color = color;
        this.bgcolor = bgcolor;
    }
    
    public ColoredChar (char ch, Color color)
    {
    	this(ch, color, Color.black);
    }

    /**
     * Returns the {@code char} value of the {@code ColoredChar}
     * @return the {@code char} value of the {@code ColoredChar}
     */
    public char ch()
    {
        return ch;
    }

    /**
     * Returns the {@code Color} value of the {@code ColoredChar}
     * @return the {@code Color} value of the {@code ColoredChar}
     */
    public Color color()
    {
        return color;
    }
    
    public Color bgcolor()
    {
    	return bgcolor;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof ColoredChar)
        {
            ColoredChar other = (ColoredChar)obj;
            return ch == other.ch && color.equals(other.color)
            		&& bgcolor.equals(other.bgcolor);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return (color.hashCode() << 8) | (ch & 0xFF) | (bgcolor.hashCode () << 24);
    }

    @Override
    public String toString()
    {
        return Character.toString(ch);
    }
}
