/*
 * SplitArcPointAction.java
 *
 * Created on 21-Jun-2005
 */
package pige.gui.action;

import java.awt.event.ActionEvent;

import pige.dataLayer.GraphArcPathPoint;
import pige.gui.CreateGraphGui;

/**
 * @author Nadeem
 *
 * This class is used to split a point on an arc into two to  allow the arc to 
 * be manipulated further.
 */
public class SplitArcPointAction 
        extends javax.swing.AbstractAction {
   
   private GraphArcPathPoint arcPathPoint;
   
   
   public SplitArcPointAction(GraphArcPathPoint _arcPathPoint) {
      arcPathPoint = _arcPathPoint;
   }
   
   
   public void actionPerformed(ActionEvent e) {
      CreateGraphGui.getView().getUndoManager().addNewEdit(
               arcPathPoint.splitPoint());
   }
   
}
