package jade.core;

import jade.util.Guard;
import jade.util.Lambda;
import jade.util.Lambda.FilterFunc;
import jade.util.Lambda.MapFunc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The base class of both {@code Actor} and {@code World}. Allows for easy passing and aggregation
 * of {@code String} messages.
 */
public abstract class Messenger
{
    private String name = "NoName";
    private List<Message> cache;

    public Messenger(){
        cache = new ArrayList<Message>();
    }
    
    /**
     * Creates a new {@code Messenger}
     */
    public Messenger(String name)
    {
    	this();
    	setName(name);
    }

    /**
     * Appends a message, which will have this {@code Messenger} as a source.
     * 
     * @param message the message to append
     */
    public void appendMessage(String message)
    {
    	appendMessage(message, this);
    }
    
    /**
     * Appends a message, which will have this {@code Messenger} as a source.
     * @param message the message to append
     * @param source the source
     */
    public void appendMessage(String message, Messenger source)
    {
    	appendMessage(message, source, false);
    }
    
    /**
     * Appends a message, which will have this {@code Messenger} as a source.
     * @param message the message to append
     * @param source the source
     */
    public void appendMessage(String message, boolean important)
    {
    	appendMessage(message, this, important);
    }

    /**
     * Appends a message, which will have this {@code Messenger} as a source.
     * @param message the message to append
     * @param source the source
     * @param important if the message will be shown ingame
     */
    public void appendMessage(String message, Messenger source, boolean important)
    {
        Guard.argumentIsNotNull(message);

        cache.add(new Message(message, source, important));
    }
    
    /**
     * Retrieves and clears all messages held by the {@code Messenger}.
     * 
     * @return all messages held by the {@code Messenger} as String
     */
    public Iterable<String> retrieveMessages()
    {
        Iterable<String> messages = Lambda.map(cache, SelectTextLambda());
        cache.clear();
        return messages;
    }
    
    /**
     * Returns the iterator of the {@code Messenger}.
     * 
     * @return iterator over all messages held by the {@code Messenger} 
     */
    public Message getNextMessage()
    {
    	if(cache.isEmpty())
    		return null;
    	Message m = cache.get(0);
    	cache.remove(0);
    	return m;
    }
    
    public boolean hasNextMessage(){
    	return !cache.isEmpty();
    }

    public void setName(String name){
    	this.name = name;
    }
    
    public String getName(){
    	return name;
    }
    
    /**
     * Moves the messages stored by the given {@code Messenger} into this one. The source of each
     * message is preserved.
     * 
     * @param messenger the messenger who's messages will be retrieved
     */
    public void aggregateMessages(Messenger messenger)
    {
        Guard.argumentIsNotNull(messenger);

        cache.addAll(messenger.cache);
        messenger.clearMessages();
    }

    /**
     * Filters out messages whose source {@code Messenger} is not in the provided filter.
     * 
     * @param filter the group of {@code Messenger} whose messages will be preserved
     */
    public void filterMessages(Collection<Messenger> filter)
    {
        Guard.argumentIsNotNull(filter);

        Iterable<Message> filtered = Lambda.filter(cache, FilterSourceLambda(filter));
        cache.clear();
        for(Message message : filtered)
            cache.add(message);
    }

    /**
     * Removes all messages held by the {@code Messenger}
     */
    public void clearMessages()
    {
        cache.clear();
    }

    public class Message
    {
        public final String text;
        public final Messenger source;
        public final boolean important;
        
        public Message(String text, Messenger source){
        	this(text, source, false);
        }

        public Message(String text, Messenger source, boolean important)
        {
            this.text = text;
            this.source = source;
            this.important = important;
        }
    }

    private static MapFunc<Message, String> SelectTextLambda()
    {
        return new MapFunc<Messenger.Message, String>()
        {
            @Override
            public String map(Message element)
            {
                return element.text;
            }
        };
    }

    private FilterFunc<Message> FilterSourceLambda(final Collection<Messenger> filter)
    {
        return new FilterFunc<Message>()
        {
            @Override
            public boolean filter(Message element)
            {
                return filter.contains(element.source);
            }
        };
    }
}
