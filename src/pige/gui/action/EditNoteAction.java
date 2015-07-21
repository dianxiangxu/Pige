/*
 * EditNoteAction.java
 */
package pige.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pige.dataLayer.Note;


public class EditNoteAction 
        extends AbstractAction {

   private Note selected;
   

   public EditNoteAction(Note component) {
      selected = component;
   }

   
   /** Action for editing the text in a Note */
   public void actionPerformed(ActionEvent e) {
      selected.enableEditMode();
   }

}
