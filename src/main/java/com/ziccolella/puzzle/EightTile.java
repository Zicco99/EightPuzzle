package com.ziccolella.puzzle;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.JButton;
import javax.swing.border.LineBorder;

import com.ziccolella.puzzle.Events_and_Listeners.EightRestart;

public class EightTile extends JButton implements EightRestart.Listener,PropertyChangeListener{
  //By analysing stacktrace and source code i discovered that is not necessary to initialize property veto and change supports
  //as JBUTTON and other JComponents initialize them in their costructors, so a super() call is enough;

  int position; //It's a constant
  boolean position_has_been_initialized;

  int label; //It's a Bound and Constrained property

  //Constructors must have 0 parameters constructors 
  public EightTile(){
    super();

    //Define button shape
    this.setBorder( new LineBorder(Color.DARK_GRAY) );
    this.setFont(new Font("Arial", Font.PLAIN, 20));

    //Add behaviour
    this.addActionListener(e -> { try { super.fireVetoableChange("VETO_MOVE_EVENT",this.label,9); } catch (PropertyVetoException ev){}});
    
    //Declare the support variable to make <position> writable only once
    position_has_been_initialized = false;
  }

  public int getPosition(){ // getter
    return this.position;
    }      

  public void setPosition(int p){ //setter
    if (!position_has_been_initialized){
      this.position = p;
      position_has_been_initialized=true;
    }
  }

  public String getLabel(){ // getter
  return Integer.toString(label);
  }

  public void setLabel(int lab){ // setter 
    this.label = lab;
    this.setText(this.getLabel());
    if(label==9){
      this.setText("");
      this.setEnabled(false);
      this.setBackground(Color.GRAY);
    }
    else{
      if(position==label-1) this.setBackground(Color.GREEN);
      else this.setBackground(Color.YELLOW);
      this.setEnabled(true);
    }
  }

  @Override //Function that will be executed when a restart event occurs
  public void restart(EightRestart.Event e) {
    this.setLabel(e.payload.get(position));
  }

  @Override
  public void propertyChange(PropertyChangeEvent e) { //Function that will be executed when update title event occurs (tile click -> veto event -> update event)
    if(e.getPropertyName()=="LABEL_UPDATE_EVENT"){

      if((Integer)e.getNewValue()==this.label){
        setLabel((Integer)e.getOldValue());
      }
      else if((Integer)e.getOldValue()==this.label){
        setLabel((Integer)e.getNewValue());
      }
      
    }
  }
}
