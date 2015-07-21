/*
 * Created on 04-Mar-2004
 * Author is Michael Camacho
 *
 */
package pige.gui.action;

import java.awt.event.ActionEvent;

import pige.dataLayer.GraphArcPathPoint;
import pige.gui.CreateGraphGui;


public class ToggleArcPointAction 
        extends javax.swing.AbstractAction {

   private GraphArcPathPoint arcPathPoint;

   
   public ToggleArcPointAction(GraphArcPathPoint _arcPathPoint) {
      arcPathPoint = _arcPathPoint;
   }

   
   /* (non-Javadoc)
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
   public void actionPerformed(ActionEvent e) {
      CreateGraphGui.getView().getUndoManager().addNewEdit(
              arcPathPoint.togglePointType());
   }

}
