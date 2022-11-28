package com.ziccolella.puzzle;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.ziccolella.puzzle.Events_and_Listeners.*;

//This buddy implemens the logic:
//Receives an veto move event when a tile is clicked 
//Checks if it is a valid move 
//Send move update event to all tiles (each tile once received the move will change its state only if it is involved) 

public class EightController extends JLabel implements VetoableChangeListener {
    private static final int ROWS = 3;
    private static final int COLS = 3;

    private ArrayList<EightRestart.Listener> restart_listeners = new ArrayList<>();
    private ArrayList<EightTile> tiles = new ArrayList<>();
    private ArrayList<Integer> current_conf;
    private HashMap<String, Direction> moves;

    public class Direction {
        final int x;
        final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public EightController() {
        this.setText("START");
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setFont(new Font("Arial", Font.PLAIN, 15));
        this.setForeground(Color.WHITE);

        moves = new HashMap<String, Direction>();
        moves.put("left", new Direction(-1, 0));
        moves.put("right", new Direction(1, 0));
        moves.put("up", new Direction(0, -1));
        moves.put("down", new Direction(0, 1));
    }

    public void add_tile(EightTile t) {
        // Save reference to the tile
        tiles.add(t);

        // Tile sends the event to the controller (veto_event)
        t.addVetoableChangeListener(this);

        // Controller sends event to the tile
        this.addPropertyChangeListener(t); // (tile_event_update)
        this.addEightRestartListener(t); // (restart)
    }

    // Veto implementation
    public void vetoableChange(PropertyChangeEvent e) {
        if (e.getPropertyName() == "VETO_MOVE_EVENT" || e.getPropagationId() == this) {

            EightTile clicked_tile = (EightTile) e.getSource();// The clicked tile can be taken by the event
            EightTile hole_tile = tiles.get(current_conf.indexOf(e.getNewValue()));// The hole tile can be taken by the
                                                                                   // local cache (current_conf)

            int c_pos = clicked_tile.getPosition();
            int h_pos = hole_tile.getPosition();

            // Using the position exclude moves not allowed
            HashMap<String, Direction> allowed_moves = new HashMap<>();
            allowed_moves.putAll(moves);

            if (c_pos < COLS * 1)
                allowed_moves.remove("up"); // if c_pos is in first row exclude up move
            if (c_pos >= COLS * (ROWS - 1))
                allowed_moves.remove("down"); // if c_pos is in last row exclude down move

            if (c_pos % COLS == 0)
                allowed_moves.remove("left"); // if c_pos is in first column row exclude left move
            if (c_pos % COLS == COLS - 1)
                allowed_moves.remove("right"); // if c_pos is in last last column exclude right move

            // Check if hole is adacient and reachable
            for (Direction d : allowed_moves.values()) {
                int possible_move_p = c_pos + d.x + d.y * COLS;
                if (h_pos == possible_move_p) {

                    // Update cache structure
                    Collections.swap(current_conf, current_conf.indexOf(e.getOldValue()),current_conf.indexOf(e.getNewValue()));
                    // Send all tiles the changement (Broadcast)
                    this.firePropertyChange("LABEL_UPDATE_EVENT", e.getNewValue(), e.getOldValue());
                    this.setText("OK");

                    //if the move has been done by the player
                    if(e.getPropagationId() != this){
                        for(EightTile tile : tiles){
                            if(tile.label!=9 && tile.getBackground()!=Color.GREEN){ //If all tiles are green , 9 not included -> player won.
                                return;
                            }
                        }
                    }
                    this.setText("YOU WON :)");
                    return;
                }
            }
            this.setText("KO");
        }
    }

    public void restart() {
        // Initialize the array of labels
        current_conf = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        // Propagate reset conf to tiles
        restart_listeners.forEach(
                (EightRestartListener) -> EightRestartListener.restart(new EightRestart.Event(this, current_conf)));

        // Let's exploit the already written code,
        // we are gonna simulate multiple click tile events
        // (Veto algorithm will discard wrong one)
        // in order to shuffle the board
        for (int z = 0; z < 100000; z++) {
            int sim = ThreadLocalRandom.current().nextInt(0, 9);
            // Simulted ev, PropagationID has been released for future use, even if it's
            // usally by some projects to identify a event stream,
            // he we will use it to identify this call as a special event 
            PropertyChangeEvent ev = new PropertyChangeEvent(tiles.get(sim), "VETO_MODE_EVENT", sim, 9);
            ev.setPropagationId(this);
            vetoableChange(ev);
        }

        this.setText("RESETTED");
    }

    public void flip(java.awt.event.ActionEvent e) {
        this.setText("CHECK CODE :)");
        //Don't need the flip function, this game is deterministic and each action or sequence of actions is reversible.
        //This way of shuffling, doing a sequence of actions starting from the solution (X) to reach conf (Y),
        //ensure us that by doing the same sequence reversed we will obtain (X), the solution.
    }

    //Utility function
    public boolean check_solvable() {
        int inv = 0;
        for (int i = 0; i < current_conf.size() - 1; i++) {
            for (int j = i + 1; j < current_conf.size(); j++) {
                if (current_conf.get(i) > current_conf.get(i))
                    inv++;
            }
        }
        return inv % 2 == 0;
    }

    public synchronized void addEightRestartListener(EightRestart.Listener l) {
        restart_listeners.add(l);
    }

    public synchronized void removeEightRestartListener(EightRestart.Listener l) {
        restart_listeners.remove(l);
    }

    /*
     * DOCUMENTATION USEFUL:
     * A "PropertyChange" event gets delivered whenever a bean changes a "bound" or
     * "constrained" property.
     * A PropertyChangeEvent object is sent as an argument to the
     * PropertyChangeListener and VetoableChangeListener methods.
     * If the new value is a primitive type (such as int or boolean) it must be
     * wrapped as the corresponding java.lang.* Object type (such as Integer or
     * Boolean).
     * Null values may be provided for the old and the new values if their true
     * values are not known.
     */
}
