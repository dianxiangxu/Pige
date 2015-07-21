/*
 * Created on 04-Mar-2004
 * Author is Michael Camacho
 *
 */
package pige.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pige.dataLayer.GraphObject;
import pige.gui.CreateGraphGui;


public class DeleteGraphObjectAction 
        extends AbstractAction {

   private GraphObject selected;

   
   public DeleteGraphObjectAction(GraphObject component) {
      selected = component;
   }

   /* (non-Javadoc)
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
   public void actionPerformed(ActionEvent e) {
      CreateGraphGui.getView().getUndoManager().newEdit(); // new "transaction""
      CreateGraphGui.getView().getUndoManager().deleteSelection(selected);      
      selected.delete();
   }

}
