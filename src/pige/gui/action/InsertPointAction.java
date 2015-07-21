/*
 * InsertPointAction.java
 */
package pige.gui.action;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import pige.dataLayer.GraphArc;
import pige.dataLayer.GraphArcPathPoint;
import pige.gui.CreateGraphGui;


/**
 * This class is used to split an arc in two at the
 * point the user clicks the mouse button.
 * @author Pere
 */
public class InsertPointAction 
        extends javax.swing.AbstractAction{
   
   private GraphArc selected;
   Point2D.Float mouseposition;
   
   
   public InsertPointAction(GraphArc arc, Point mousepos) {
      selected = arc;
      
      // Mousepos is relative to selected component i.e. the arc
      // Need to convert this into actual coordinates
      Point2D.Float offset = new Point2D.Float(selected.getX(), 
                                               selected.getY());
      mouseposition = new Point2D.Float(mousepos.x + offset.x, 
                                        mousepos.y + offset.y);
   }
   
   
   public void actionPerformed(ActionEvent arg0) {
      CreateGraphGui.getView().getUndoManager().addNewEdit(
//              selected.getArcPath().insertPointAt(mouseposition, false));
              selected.getArcPath().insertPointAt(mouseposition, GraphArcPathPoint.CURVED));
   }
   
}
