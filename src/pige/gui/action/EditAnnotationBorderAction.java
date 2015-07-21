/*
 * Created on 07-Mar-2004
 * Author is Michael Camacho
 */
package pige.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pige.dataLayer.GraphAnnotationNote;
import pige.gui.CreateGraphGui;


public class EditAnnotationBorderAction
        extends AbstractAction {

   private GraphAnnotationNote selected;
   

   public EditAnnotationBorderAction(GraphAnnotationNote component) {
      selected = component;
   }

      
   /** Action for editing the text in an AnnotationNote */
   public void actionPerformed(ActionEvent e) {
      CreateGraphGui.getView().getUndoManager().addNewEdit(
               selected.showBorder(!selected.isShowingBorder()));
   }

}
