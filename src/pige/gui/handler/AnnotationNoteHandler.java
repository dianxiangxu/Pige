 /*
 * Created on 05-Mar-2004
 * Author is Michael Camacho
 *
 */
package pige.gui.handler;

import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import pige.dataLayer.GraphAnnotationNote;
import pige.gui.action.EditAnnotationBackgroundAction;
import pige.gui.action.EditAnnotationBorderAction;
import pige.gui.action.EditNoteAction;


public class AnnotationNoteHandler 
        extends NoteHandler {
   

   public AnnotationNoteHandler(Container contentpane, GraphAnnotationNote note) {
      super(contentpane, note);
      enablePopup = true;
   }

   
   /** 
    * Creates the popup menu that the user will see when they right click on a 
    * component */
   public JPopupMenu getPopup(MouseEvent e) {
      int popupIndex = 0;
      JPopupMenu popup = super.getPopup(e);
      
      JMenuItem menuItem =
              new JMenuItem(new EditNoteAction((GraphAnnotationNote)myObject));
      menuItem.setText("Edit text");
      popup.insert(menuItem, popupIndex++);
      
      menuItem = new JMenuItem(
              new EditAnnotationBorderAction((GraphAnnotationNote)myObject));
      if (((GraphAnnotationNote)myObject).isShowingBorder()){
         menuItem.setText("Disable Border");
      } else{
         menuItem.setText("Enable Border");
      }
      popup.insert(menuItem, popupIndex++);
      
      menuItem = new JMenuItem(
              new EditAnnotationBackgroundAction((GraphAnnotationNote)myObject));
      if (((GraphAnnotationNote)myObject).isFilled()) {
         menuItem.setText("Transparent");
      } else {
         menuItem.setText("Solid Background");
      }
      popup.insert(new JPopupMenu.Separator(), popupIndex++);      
      popup.insert(menuItem, popupIndex);

      return popup;
   }

   
   public void mouseClicked(MouseEvent e) {
      if ((e.getComponent() == myObject || !e.getComponent().isEnabled()) && 
              (SwingUtilities.isLeftMouseButton(e))) { 
         if (e.getClickCount() == 2){
            ((GraphAnnotationNote)myObject).enableEditMode();
         }
      }
   }
   
}
