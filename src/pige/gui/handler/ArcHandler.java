package pige.gui.handler;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import pige.dataLayer.GraphArc;
import pige.dataLayer.GraphArcPathPoint;
import pige.gui.Constants;
import pige.gui.CreateGraphGui;
import pige.gui.Grid;
import pige.gui.GuiView;
import pige.gui.action.SplitArcAction;
import pigelocales.PigeLocales;

public class ArcHandler extends GraphObjectHandler {

   
   public ArcHandler(Container contentpane, GraphArc obj) {
      super(contentpane, obj);
      enablePopup = true;
   }

   public JPopupMenu getPopup(MouseEvent e) {
      int popupIndex = 0;
      JMenuItem menuItem;
      JPopupMenu popup = super.getPopup(e);      
 
      if (myObject instanceof GraphArc) {
    	  if (CreateGraphGui.graphType.hasArcProperty()){
    		  final GraphArc currentArc = ((GraphArc)myObject);
    		  menuItem = new JMenuItem(PigeLocales.bundleString("Edit "+CreateGraphGui.graphType.getArcTitle()));      
    		  menuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                   currentArc.showEditor();
                }
              });       
    		  popup.insert(menuItem, popupIndex++);
    		  
    		  menuItem = new JMenuItem((currentArc.showArcProperty()?PigeLocales.bundleString("Hide"): PigeLocales.bundleString("Show")) 
    			  + " "+PigeLocales.bundleString("Pre-Post-condition"));      
    		  menuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                   currentArc.toggleShowArcProperty();
                }
              });       
    		  popup.insert(menuItem, popupIndex++);
    	   }
           menuItem = new JMenuItem(new SplitArcAction((GraphArc)myObject, 
                                                         e.getPoint()));            
           menuItem.setText(PigeLocales.bundleString("Insert Point"));
           popup.insert(menuItem, popupIndex++);

           popup.insert(new JPopupMenu.Separator(), popupIndex);
      }
      return popup;
   }

   
   public void mousePressed(MouseEvent e) {
      super.mousePressed(e);
      if (CreateGraphGui.getGraphPanel().isEditionAllowed() == false){
         return;
      }      
      if (e.getClickCount() == 2){
        GraphArc arc = (GraphArc)myObject;
/*         if (e.isControlDown()){
            CreateGraphGui.getView().getUndoManager().addNewEdit(
                    arc.getArcPath().insertPointAt(
                            new Point2D.Float(arc.getX() + e.getX(), 
                            arc.getY() + e.getY()), e.isAltDown()));
         } else {
            arc.getSource().select();
            arc.getTarget().select();
            justSelected = true;
         }
*/     if (CreateGraphGui.graphType.hasArcProperty())  
        	arc.showEditor();
      }
   }

   
   public void mouseDragged(MouseEvent e) {
      switch (CreateGraphGui.getGraphPanel().getMode()) {
         case Constants.SELECT:
            if (!isDragging){
               break;
            }
            GraphArc currentObject = (GraphArc)myObject;
            Point oldLocation = currentObject.getLocation();
            // Calculate translation in mouse
            int transX = (int)(Grid.getModifiedX(e.getX() - dragInit.x));
            int transY = (int)(Grid.getModifiedY(e.getY() - dragInit.y));
            ((GuiView)contentPane).getSelectionObject().translateSelection(
                     transX, transY);
            dragInit.translate(
                     -(currentObject.getLocation().x - oldLocation.x - transX),
                     -(currentObject.getLocation().y - oldLocation.y - transY));
      }
   }

   public void mouseWheelMoved (MouseWheelEvent e) {
      
   }
   
}
