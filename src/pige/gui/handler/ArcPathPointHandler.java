/*
 * Created on 28-Feb-2004
 * Author is Michael Camacho
 *
 */
package pige.gui.handler;

import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import pige.dataLayer.GraphArcPathPoint;
import pige.gui.CreateGraphGui;
import pige.gui.action.SplitArcPointAction;
import pige.gui.action.ToggleArcPointAction;
import pigelocales.PigeLocales;


public class ArcPathPointHandler 
        extends GraphObjectHandler {

   
   public ArcPathPointHandler(Container contentpane, GraphArcPathPoint obj) {
      super(contentpane, obj);
      enablePopup = true;
   }
   
   
   /** Creates the popup menu that the user will see when they right click on a component */
   public JPopupMenu getPopup(MouseEvent e) {
      JPopupMenu popup = super.getPopup(e);
      
      if (!((GraphArcPathPoint)myObject).isDeleteable()) {
         popup.getComponent(0).setEnabled(false);
      }
      
      popup.insert(new JPopupMenu.Separator(), 0);
      
      if (((GraphArcPathPoint)myObject).getIndex()==0) {
         return null;
      } else {
         JMenuItem menuItem = 
                 new JMenuItem(new ToggleArcPointAction((GraphArcPathPoint)myObject));
         if (((GraphArcPathPoint)myObject).getPointType() == GraphArcPathPoint.STRAIGHT) {
            menuItem.setText(PigeLocales.bundleString("Change to Curved"));
         } else{
            menuItem.setText(PigeLocales.bundleString("Change to Straight"));
         }
         popup.insert(menuItem,0);
         
         menuItem = new JMenuItem(new SplitArcPointAction((GraphArcPathPoint)myObject));
         menuItem.setText(PigeLocales.bundleString("Split Point"));
         popup.add(menuItem,1);
         
         // The following commented out code can be used for
         // debugging arc issues - Nadeem 18/07/2005
         /*
         menuItem = new JMenuItem(new GetIndexAction((ArcPathPoint)myObject,
                                                     e.getPoint()));
         menuItem.setText("Point Index");
         menuItem.setEnabled(false);
         popup.add(menuItem);
          */
      }
      return popup;
   }
   
   
   public void mousePressed(MouseEvent e) {
      if (myObject.isEnabled()) {
         ((GraphArcPathPoint)e.getComponent()).setVisibilityLock(true);
         super.mousePressed(e);
      }
   }
   
   
   public void mouseDragged(MouseEvent e) {
      super.mouseDragged(e);
   }
   
   
   public void mouseReleased(MouseEvent e) {
      ((GraphArcPathPoint)e.getComponent()).setVisibilityLock(false);
      super.mouseReleased(e);
   }
   
   
   public void mouseWheelMoved (MouseWheelEvent e) { 
      
      if (CreateGraphGui.getGraphPanel().isEditionAllowed() == false ||  //NOU-PERE
              e.isControlDown()) {
         return;
      }
      
      if (e.isShiftDown()) {
         CreateGraphGui.getView().getUndoManager().addNewEdit(
                 ((GraphArcPathPoint)myObject).togglePointType());
      }
   }  
   
}
