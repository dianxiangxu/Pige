/*
 * Created on 
 * Author is 
 *
 */
package pige.gui.handler;

import java.awt.Container;
import java.awt.event.MouseEvent;

import pige.dataLayer.Note;


public class NoteHandler 
        extends GraphObjectHandler {
   
   
   public NoteHandler(Container contentpane, Note note) {
      super(contentpane, note);
      enablePopup = true;
   }

   
   public void mousePressed(MouseEvent e) {      
      if ((e.getComponent() == myObject) || !e.getComponent().isEnabled()){
         super.mousePressed(e);
      }
   }

   
   public void mouseDragged(MouseEvent e) {
      if ((e.getComponent() == myObject) || !e.getComponent().isEnabled()){
         super.mouseDragged(e);
      }
   }

   
   public void mouseReleased(MouseEvent e) {
      if ((e.getComponent() == myObject) || !e.getComponent().isEnabled()){
         super.mouseReleased(e);
      }
   }
   
}
