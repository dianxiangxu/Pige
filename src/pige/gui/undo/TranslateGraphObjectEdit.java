
package pige.gui.undo;

import pige.dataLayer.GraphObject;


/**
 *
 * @author Pere Bonet
 */
public class TranslateGraphObjectEdit 
        extends UndoableEdit {
   
   GraphObject pnObject;
   Integer transX;
   Integer transY;
   
   
   /** Creates a new instance of  */
   public TranslateGraphObjectEdit(GraphObject _pnObject,
                                      Integer _transX, Integer _transY) {
      pnObject = _pnObject;
      transX = _transX;
      transY = _transY;
   }

   
   /** */
   public void undo() {
      pnObject.translate(-transX, -transY);
   }

   
   /** */
   public void redo() {
      pnObject.translate(transX, transY);
   }

   
   public String toString(){
      return super.toString()  + " " + pnObject.getName() + 
              " (" + transX + "," + transY + ")";
   }
   
}
