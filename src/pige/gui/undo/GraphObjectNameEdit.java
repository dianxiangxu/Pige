
package pige.gui.undo;

import pige.dataLayer.GraphObject;


/**
 *
 * @author corveau
 */
public class GraphObjectNameEdit 
        extends UndoableEdit {
   
   GraphObject graphObject;
   String oldName;
   String newName;
   
   
   /** Creates a new instance of placeNameEdit */
   public GraphObjectNameEdit(GraphObject _graphObject,
                            String _oldName, String _newName) {
      graphObject = _graphObject;
      oldName = _oldName;      
      newName = _newName;
   }

   
   /** */
   public void undo() {
      graphObject.setName(oldName);
   }

   
   /** */
   public void redo() {
      graphObject.setName(newName);
   }
   
}
