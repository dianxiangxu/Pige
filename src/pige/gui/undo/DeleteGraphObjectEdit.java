package pige.gui.undo;

import pige.dataLayer.GraphDataLayerInterface;
import pige.dataLayer.GraphObject;
import pige.gui.GuiView;


/**
 *
 * @author Pere Bonet
 */
public class DeleteGraphObjectEdit 
        extends UndoableEdit {
   
   GraphObject pnObject;
   GraphDataLayerInterface model;
   GuiView view;
   Object[] objects;
   
   
   /** Creates a new instance of placeWeightEdit */
   public DeleteGraphObjectEdit(GraphObject _pnObject,
            GuiView _view, GraphDataLayerInterface _model) {
      pnObject = _pnObject;
      view = _view;
      model = _model;

      pnObject.markAsDeleted();      
   }

     
   /** */
   public void redo() {
      pnObject.delete();
   }

   
   /** */
   public void undo() {
      pnObject.undelete(model,view);
   }
   
   
   public String toString(){
      return super.toString() + " " + pnObject.getClass().getSimpleName() 
             + " [" +  pnObject.getId() + "]";
   }   
   
}
