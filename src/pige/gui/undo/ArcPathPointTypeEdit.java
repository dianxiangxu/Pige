/*
 * ArcPathPointTypeEdit.java
 */

package pige.gui.undo;

import pige.dataLayer.GraphArcPathPoint;

/**
 *
 * @author corveau
 */
public class ArcPathPointTypeEdit 
        extends UndoableEdit {
   
   GraphArcPathPoint arcPathPoint;
  
   
   /** Creates a new instance of placeWeightEdit */
   public ArcPathPointTypeEdit(GraphArcPathPoint _arcPathPoint) {
      arcPathPoint = _arcPathPoint;
   }
   
   
   /** */
   public void undo() {
      arcPathPoint.togglePointType();
   }

   
   /** */
   public void redo() {
      arcPathPoint.togglePointType();
   }

   
   
   public String toString(){
      return super.toString() + " " + arcPathPoint.getName();
   }
      
}
