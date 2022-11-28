package com.ziccolella.puzzle;

import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;;

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
        this.getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        this.getRootPane().setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, Color.LIGHT_GRAY));
        this.setTitle("8Puzzle");
        ControlPanel cp = new ControlPanel();
        BoardGrid board = new BoardGrid();
        this.add(board);
        this.add(cp);
        this.pack();
    }

    //Defining the 3x3 Grid
    class BoardGrid extends JPanel{

        public BoardGrid() {
            //Define shape/dims/etc.
            this.setPreferredSize(new Dimension(350,350));
            this.setBorder(BorderFactory.createMatteBorder(6, 6, 6, 6, Color.BLACK));
            this.setBackground(Color.DARK_GRAY);

            //From docu parameters are (int rows, int cols, int hgap, int vgap)
            this.setLayout(new GridLayout(ROWS,COLS,0,0));
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
            //Define shape/dims/etc.
            this.setPreferredSize(new Dimension(350,60));
            this.setBackground(Color.DARK_GRAY);
            this.setBorder(BorderFactory.createMatteBorder(6, 6, 6, 6, Color.LIGHT_GRAY));

            //From docu parameters are (int rows, int cols, int hgap, int vgap)
            this.setLayout(new GridLayout( 1, 3, 0, 0));

            controller = new EightController();
            this.add(controller);

            restart_butt = new JButton("Restart");
            restart_butt.setBackground(Color.orange);
            restart_butt.setFont(new Font("Arial", Font.PLAIN, 20));
            restart_butt.addActionListener(e -> controller.restart());
            this.add(restart_butt);

            flip_butt = new JButton("Flip");
            flip_butt.setBackground(Color.blue);
            flip_butt.setFont(new Font("Arial", Font.PLAIN, 20));
            flip_butt.addActionListener(e -> controller.flip(e));
            this.add(flip_butt);

        }
    }

}



