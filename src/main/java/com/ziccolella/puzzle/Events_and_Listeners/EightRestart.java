package com.ziccolella.puzzle.Events_and_Listeners;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Event + listener implementation that allows player to reset the game.
 * @author zicco
 */

public class EightRestart{
    // This interface defines the listener methods that any event
    // listeners for “EightRestart” events must support.
    public interface Listener extends EventListener{
        public void restart(EightRestart.Event e);
    }
    
    // This class defines the state object associated with the event
    public static class Event extends EventObject {
        public ArrayList<Integer> payload;

        public Event(Object src,ArrayList<Integer> perm) {
            super(src);
            this.payload = perm;
        }
    
    }


}