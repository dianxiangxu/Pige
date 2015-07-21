/*
 * DeleteArcPathPointEdit.java
 */

package pige.gui.undo;

import pige.dataLayer.GraphArc;
import pige.dataLayer.GraphArcPath;
import pige.dataLayer.GraphArcPathPoint;

/**
 *
 * @author Pere Bonet
 */
public class DeleteArcPathPointEdit
        extends UndoableEdit {
   
   GraphArcPath arcPath;
   GraphArcPathPoint point;
   Integer index;

   /** Creates a new instance of placeWeightEdit */
   public DeleteArcPathPointEdit(GraphArc _arc, GraphArcPathPoint  _point, Integer _index) {
      arcPath = _arc.getArcPath();
      point = _point;
      index = _index;
   }

   
   /** */
   public void undo() {
      arcPath.insertPoint(index, point);
      arcPath.updateArc();      
   }

   
   /** */
   public void redo() {
      point.delete();
   }
   
}
