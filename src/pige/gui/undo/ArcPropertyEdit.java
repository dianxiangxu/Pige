
package pige.gui.undo;

import pige.dataLayer.GraphArc;

public class ArcPropertyEdit 
        extends UndoableEdit {
   
   private GraphArc arc;
   private String oldName;
   private String newName;
   
   private String oldPrecond;
   private String newPrecond;
   
   private String oldPostcond;
   private String newPostcond;
   
   public ArcPropertyEdit(GraphArc _arc,
                            String _oldName, String _newName,
                            String _oldPrecond, String _newPrecond,
                            String _oldPostcond, String _newPostcond) {
      arc = _arc;
      oldName = _oldName;      
      newName = _newName;
      oldPrecond = _oldPrecond;
      newPrecond = _newPrecond;
      oldPostcond = _oldPostcond;
      newPostcond = _newPostcond;
      
   }

   
   public void undo() {
      arc.setName(oldName);
      arc.setPrecondition(oldPrecond);
      arc.setPostcondition(oldPostcond);
      arc.update();
   }

   
   public void redo() {
      arc.setName(newName);
      arc.setPrecondition(newPrecond);
      arc.setPostcondition(newPostcond);
      arc.update();
   }
   
}
