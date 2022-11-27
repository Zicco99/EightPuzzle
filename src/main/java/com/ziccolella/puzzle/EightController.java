package com.ziccolella.puzzle;

import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JLabel;

import com.ziccolella.puzzle.Events_and_Listeners.*;

//This buddy must check if a move is legal or not
public class EightController extends JLabel implements VetoableChangeListener{
    private static final int ROWS = 3;
    private static final int COLS = 3;

    private ArrayList<EightRestart.Listener> restart_listeners = new ArrayList<>();
    public ArrayList<EightTile> tiles = new ArrayList<>();
    private ArrayList<Integer> current_conf;
    private HashMap<String,Direction> moves;

    public class Direction{
        final int x;
        final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public EightController() {
        this.setText("START");
        moves = new HashMap<String,Direction>();
        moves.put("left",new Direction(-1, 0));
        moves.put("right",new Direction(1, 0));
        moves.put("up",new Direction(0, -1));
        moves.put("down",new Direction(0, 1));
    }

    public void add_tile(EightTile t){

        //Save reference to the tile
        tiles.add(t);

        //Tile sends the event to the controller
        t.addVetoableChangeListener(this);

        //Controller sends the event to the tile
        this.addPropertyChangeListener(t);
        this.addEightRestartListener(t);
        System.out.println("lol");
    }

    //Veto implementation
    public void vetoableChange(PropertyChangeEvent e){
        if(e.getPropertyName()=="VETO_MOVE_EVENT"){

            System.out.println("I'm the controller, lemme check, one sec");
            System.out.println("First, i need to check if clicked tile is adiancent to the hole (the one that has 9 as label)");

            EightTile clicked_tile = (EightTile)e.getSource();//The clicked tile can be taken by the event
            EightTile hole_tile = tiles.get(current_conf.indexOf(e.getNewValue()));//The hole tile can be taken by the local structure

            int c_pos = clicked_tile.getPosition();
            int h_pos = hole_tile.getPosition();

            //Using the position exclude moves not allowed 
            HashMap<String,Direction> allowed_moves = new HashMap<>();
            allowed_moves.putAll(moves);

            if(c_pos<=COLS*1) allowed_moves.remove("up"); //if c_pos is in first row exclude up move
            if(c_pos>COLS*(ROWS-1)) allowed_moves.remove("down"); //if c_pos is in last row exclude down move

            if(c_pos%COLS==0) allowed_moves.remove("left"); //if c_pos is in first column row exclude left move
            if(c_pos%COLS==2) allowed_moves.remove("right"); //if c_pos is in last last column exclude right move

            //Check if hole is adacient and reachable
            for (Direction d : allowed_moves.values()) {
                int possible_move_p = c_pos + d.x + d.y*COLS;
                if (h_pos == possible_move_p) {

                    System.out.println("Ok, go on");
                    System.out.println(current_conf);
                    Collections.swap(current_conf,current_conf.indexOf(e.getOldValue()),current_conf.indexOf(e.getNewValue()));
                    System.out.println(current_conf);

                    //Send all tiles the changement (Broadcast)
                    this.firePropertyChange("LABEL_UPDATE_EVENT",e.getNewValue(), e.getOldValue());
                    this.setText("OK");
                    return;
                }
            }
            System.out.println("Nah,hole is not adiancent");
            this.setText("KO");
        }
    }


    public void restart(){
        //Initialize the array of labels
        Integer[] l = {1,2,3,4,5,6,7,8,9};
        int temp;
    
        //SHUFFLE
        for (int i = 0; i < 1000; i++) {
            int a = ThreadLocalRandom.current().nextInt(0,9);
            int b = ThreadLocalRandom.current().nextInt(0,9);
            temp = l[a];
            l[a] = l[b];
            l[b] = temp;
        } 
        
        current_conf = new ArrayList<Integer>(Arrays.asList(l));

        this.setText("RESETTED");
    
        restart_listeners.forEach((EightRestartListener) -> EightRestartListener.restart(new EightRestart.Event(this,current_conf)));
    }

    public void flip(java.awt.event.ActionEvent e){

    }

    //Restart Event implementation

    public synchronized void addEightRestartListener(EightRestart.Listener l) {
        restart_listeners.add(l);
    }
    
    public synchronized void removeEightRestartListener(EightRestart.Listener l) {
        restart_listeners.remove(l);
    }
    

    
    /* DOCUMENTATION USEFUL TO UNDERSTAND WHAT HAPPENED:
     * A "PropertyChange" event gets delivered whenever a bean changes a "bound" or
     * "constrained" property.
     * A PropertyChangeEvent object is sent as an argument to the
     * PropertyChangeListener and VetoableChangeListener methods.
     * Normally PropertyChangeEvents are accompanied by the name and the old and new
     * value of the changed property.
     * If the new value is a primitive type (such as int or boolean) it must be
     * wrapped as the corresponding java.lang.* Object type (such as Integer or
     * Boolean).
     * Null values may be provided for the old and the new values if their true
     * values are not known.
     */
}
