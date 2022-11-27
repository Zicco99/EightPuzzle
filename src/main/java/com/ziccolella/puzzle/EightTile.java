package com.ziccolella.puzzle;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.JButton;
import com.ziccolella.puzzle.Events_and_Listeners.EightRestart;

public class EightTile extends JButton implements EightRestart.Listener,PropertyChangeListener{
  //By analysing stacktrace and source code i discovered that is not necessary to initialize property veto and change supports
  //as JBUTTON and other JComponents initialize them in their costructors, so a super() call is enough;

  int position; //It's a constant
  boolean position_has_been_initialized;
  int label; //It's a Bound and Constrained property

  //Constructors must have 0 parameters constructors 
  public EightTile(){
    super(); //
    this.addActionListener(e -> { try { super.fireVetoableChange("check_move",this.label,9); } catch (PropertyVetoException ev){}});
    position_has_been_initialized = false;
  }

  public void setPosition(int p){
    if (!position_has_been_initialized){
      this.position = p;
      position_has_been_initialized=true;
    }
  }

  public int getPosition(){ // getter
  return this.position;
  }      

  public String getLabel(){ // getter
  return Integer.toString(label);
  }

  @Override
  public void restart(EightRestart.Event e) {
      this.label = e.payload.get(position);
      this.setText(this.getLabel());
  }

  @Override
  public void propertyChange(PropertyChangeEvent e) {
    if(e.getPropertyName()=="label_update"){

      System.out.println(e.getNewValue() + " "+ e.getOldValue());

      if((Integer)e.getNewValue()==this.label){
        System.out.println("Hello from position " + this.getPosition());
        System.out.println(label + " -> " + e.getOldValue());
        this.label = (Integer)e.getOldValue();
        this.setText(this.getLabel());
      }
      if((Integer)e.getOldValue()==this.label){
        System.out.println("Hello from position " + this.getPosition());
        System.out.println(label + " -> " + e.getNewValue());
        this.label = (Integer)e.getNewValue();
        this.setText(this.getLabel());
      }
      
    }
  }
}
