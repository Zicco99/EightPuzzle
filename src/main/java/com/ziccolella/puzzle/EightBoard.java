package com.ziccolella.puzzle;

import javax.swing.*;
import java.awt.GridLayout;

public class EightBoard extends JFrame{
    private static final int ROWS = 3;
    private static final int COLS = 3;

    private EightController controller;
    private JButton restart_butt;
    private JButton flip_butt;

    public EightBoard() {
        initComponents();
        controller.restart();
    }
    
    private void initComponents() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(2, 1, 3, 3));
        ControlPanel cp = new ControlPanel();
        BoardGrid board = new BoardGrid();
        this.add(board);
        this.add(cp);
        this.pack();
    }

    //Defining the 3x3 Grid
    class BoardGrid extends JPanel{

        public BoardGrid() {

            //From docu parameters are (int rows, int cols, int hgap, int vgap)
            this.setLayout(new GridLayout(ROWS, COLS, 3, 3));

            //Initialize tiles
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {

                    //Create Eightile
                    EightTile tile = new EightTile();
                    tile.setPosition((row*COLS)+col);

                    //Give it to controller
                    controller.add_tile(tile);

                    //Add to the GUI
                    add(tile);
                }
            }

        }
    }

    //Defining the control panel
    class ControlPanel extends JPanel {
        public ControlPanel() {
            //From docu parameters are (int rows, int cols, int hgap, int vgap)
            this.setLayout(new GridLayout( 1, 3, 0, 3));

            controller = new EightController();
            this.add(controller);

            restart_butt = new JButton("Restart");
            restart_butt.addActionListener(e -> controller.restart());
            this.add(restart_butt);

            flip_butt = new JButton("Flip");
            flip_butt.addActionListener(e -> controller.flip(e));
            this.add(flip_butt);
        }
    }

}



