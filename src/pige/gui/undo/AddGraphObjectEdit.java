
package pige.gui.undo;

import pige.dataLayer.GraphDataLayerInterface;
import pige.dataLayer.GraphObject;
import pige.gui.GuiView;


/**
 *
 * @author corveau
 */
public class AddGraphObjectEdit 
        extends UndoableEdit {
   
   GraphObject pnObject;
   GraphDataLayerInterface model;
   GuiView view;
   
   
   /** Creates a new instance of placeWeightEdit */
   public AddGraphObjectEdit(GraphObject _pnObject, 
                                GuiView _view, GraphDataLayerInterface _model) {
      pnObject = _pnObject;
      view = _view;
      model = _model;
   }

   
   /** */
   public void undo() {
      pnObject.delete();
   }

   
   /** */
   public void redo() {
      pnObject.undelete(model, view);
   }
   
   
   public String toString(){
      return super.toString() + " \"" + pnObject.getName() + "\"";
   }
   
}
