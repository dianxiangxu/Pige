package pige.gui.handler;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import pige.dataLayer.GraphObject;
import pige.gui.Constants;
import pige.gui.CreateGraphGui;
import pige.gui.Grid;
import pige.gui.GuiView;
import pige.gui.action.DeleteGraphObjectAction;
import pigelocales.PigeLocales;


public class GraphObjectHandler 
        extends javax.swing.event.MouseInputAdapter { //NOU-PERE
        //implements java.awt.event.MouseWheelListener {
   
   protected Container contentPane;
   protected GraphObject myObject = null;
   
   // justSelected: set to true on press, and false on release;
   protected static boolean justSelected = false;	
   
   protected boolean isDragging = false;
   protected boolean enablePopup = false;
   protected Point dragInit = new Point();
   
   private int totalX = 0;
   private int totalY = 0;
   
   // constructor passing in all required objects
   public GraphObjectHandler(Container contentpane, GraphObject obj) {
      contentPane = contentpane;
      myObject = obj;
   }
   
   
   /** 
    * Creates the popup menu that the user will see when they right click on a 
    * component 
    */
   public JPopupMenu getPopup(MouseEvent e) {
      JPopupMenu popup = new JPopupMenu();
      JMenuItem menuItem = 
              new JMenuItem(new DeleteGraphObjectAction(myObject));
      menuItem.setText(PigeLocales.bundleString("Delete"));
      popup.add(menuItem);      
      return popup;
   }
   
   
   /** 
    * Displays the popup menu 
    */
   private void checkForPopup(MouseEvent e) {
      if (SwingUtilities.isRightMouseButton(e)){
         JPopupMenu m = getPopup(e);
         if (m != null) {
            m.show(myObject, e.getX(), e.getY());
         }
      }
   }
   
   
   public void mousePressed(MouseEvent e) {
      
      if (CreateGraphGui.getGraphPanel().isEditionAllowed() && enablePopup) { 
         checkForPopup(e);
      }
      
      if (!SwingUtilities.isLeftMouseButton(e)){ 
         return;
      }
      
      if (CreateGraphGui.getGraphPanel().getMode() == Constants.SELECT) {
         if (!myObject.isSelected()) {
            if (!e.isShiftDown()) {
               ((GuiView)contentPane).getSelectionObject().clearSelection();
            }
            myObject.select();
            justSelected = true;
         }
         dragInit = e.getPoint();
      }
   }

   
   /** 
    * Event handler for when the user releases the mouse, used in conjunction 
    * with mouseDragged and mouseReleased to implement the moving action 
    */
   public void mouseReleased(MouseEvent e) {
      // Have to check for popup here as well as on pressed for crossplatform!!
      if (CreateGraphGui.getGraphPanel().isEditionAllowed() && enablePopup){ 
         checkForPopup(e);
      }
      
      if (!SwingUtilities.isLeftMouseButton(e)){ 
         return;
      }
      
      if (CreateGraphGui.getGraphPanel().getMode() == Constants.SELECT) {
         if (isDragging) {
            isDragging = false;
            CreateGraphGui.getView().getUndoManager().translateSelection(
                        ((GuiView)contentPane).getSelectionObject().getSelection(),
                        totalX,
                        totalY);
            totalX = 0;
            totalY = 0;
         } else {
            if (!justSelected) {
               if (e.isShiftDown()) {
                  myObject.deselect();
               } else {
                  ((GuiView)contentPane).getSelectionObject().clearSelection();
                  myObject.select();
               }
            }
         }
      }
      justSelected = false;
   }
   
   
   /** 
    * Handler for dragging PlaceTransitionObjects around 
    */
   public void mouseDragged(MouseEvent e) {
     
      if (!SwingUtilities.isLeftMouseButton(e)){ 
         return;
      }
      
      if (CreateGraphGui.getGraphPanel().getMode() == Constants.SELECT) {
         if (myObject.isDraggable()) {
            if (!isDragging) {
               isDragging = true;
            }
         }

         // Calculate translation in mouse
         int transX = Grid.getModifiedX(e.getX() - dragInit.x);
         int transY = Grid.getModifiedY(e.getY() - dragInit.y);
         totalX += transX;
         totalY += transY;
         ((GuiView)contentPane).getSelectionObject().translateSelection(
                 transX, transY);
      }
   }
   
   //NOU-PERE: eliminat mouseWheelMoved   
}
