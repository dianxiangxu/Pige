/*
 * AnnotationBorderEdit.java
 */

package pige.gui.undo;

import pige.dataLayer.Note;

/**
 *
 * @author corveau
 */
public class AnnotationBorderEdit 
        extends UndoableEdit {
   
   Note note;
   
   
   /** Creates a new instance of placeRateEdit */
   public AnnotationBorderEdit(Note _note) {
      note = _note;
   }
   
   
   /** */
   public void undo() {
      note.showBorder(!note.isShowingBorder());
   }

   
   /** */
   public void redo() {
      note.showBorder(!note.isShowingBorder());
   }
   
   
   public String toString(){
      return super.toString() + " " + note.getClass().getSimpleName();
   }
   
}
