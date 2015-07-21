package pige.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pige.dataLayer.GraphAnnotationNote;


/**
 * Action to toggle the background of a note between white and transparent
 * @author Tim Kimber
 */
public class EditAnnotationBackgroundAction 
        extends AbstractAction {

   private GraphAnnotationNote note;
   
   
   public EditAnnotationBackgroundAction(GraphAnnotationNote an) {
      note = an;
   }
   
   
   public void actionPerformed(ActionEvent e) {
      note.changeBackground();
      note.repaint();
   }

}
