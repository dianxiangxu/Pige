/*
 * AddArcPathPointEdit.java
 */

package pige.gui.undo;

import pige.dataLayer.GraphArc;
import pige.dataLayer.GraphArcPath;
import pige.dataLayer.GraphArcPathPoint;


/**
 *
 * @author Pere Bonet
 */
public class AddArcPathPointEdit
        extends UndoableEdit {
   
   GraphArcPath arcPath;
   GraphArcPathPoint point;
   Integer index;

   /** Creates a new instance of AddArcPathPointEdit */
   public AddArcPathPointEdit(GraphArc _arc, GraphArcPathPoint  _point) {
      arcPath = _arc.getArcPath();
      point = _point;
      index = point.getIndex();
   }

   
   /**
    *
    */
   public void undo() {
      point.delete();
   }

   
   /** */
   public void redo() {
      arcPath.insertPoint(index, point);
      arcPath.updateArc();
   }
   
}
