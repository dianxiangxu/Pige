/*
 * CopyPasteManager.java
 */
package pige.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import pige.dataLayer.GraphDataLayerInterface;
import pige.dataLayer.GraphAbstractNode;
import pige.dataLayer.GraphArc;
import pige.dataLayer.GraphNode;
import pige.dataLayer.GraphObject;
import pige.gui.undo.AddGraphObjectEdit;
import pige.gui.undo.UndoManager;
import pige.gui.undo.UndoableEdit;


/**
 * Class to handle paste & paste functionality
 * @author Pere Bonet
 */
public class CopyPasteManager 
        extends javax.swing.JComponent 
        implements pige.gui.Zoomable, java.awt.event.MouseListener, 
                   java.awt.event.MouseMotionListener, 
                   java.awt.event.KeyListener {

   private static final Color PASTE_COLOR = new Color(155,155,155,100);
   private static final Color PASTE_COLOR_OUTLINE = new Color(155,0,0,0);
   
   private Rectangle pasteRectangle = new Rectangle(-1,-1);   
   
   // pasteInProgres is true when pasteRectangle is visible (user is doing a 
   // paste but still hasn't chosen the position where elements will be pasted).
   private boolean pasteInProgress = false;
   
   private ArrayList <ArrayList> objectsToPaste = new ArrayList();
   
   private Point origin = new Point();
   
   private GuiView sourceView;
   
   private int zoom;


   public CopyPasteManager() {
      addMouseListener(this);
      addMouseMotionListener(this);
      addKeyListener(this);
   }


   private void updateBounds() {
      if (pasteInProgress) {
         setBounds(0, 0, 
                   CreateGraphGui.getView().getWidth(), 
                   CreateGraphGui.getView().getHeight());
      }
   }

   
   public void doCopy(ArrayList<GraphObject> toCopy, GuiView _sourceView){
      
      sourceView = _sourceView;
      zoom = sourceView.getZoom();
      
      int bottom = 0;
      int right = 0;
      int top = Integer.MAX_VALUE;
      int left = Integer.MAX_VALUE;
      
      ArrayList<GraphArc> arcsToPaste = new ArrayList<GraphArc>();
      ArrayList nodesToPaste = new ArrayList();	// nodes and annotations
      
      for (GraphObject graphObject : toCopy) {
         if (graphObject.isCopyPasteable()){
            if (graphObject instanceof GraphArc) {
               arcsToPaste.add((GraphArc)graphObject.copy());
            } else {
               if (graphObject.getX() < left) {
                  left = graphObject.getX();
               }
               if (graphObject.getX() + graphObject.getWidth() > right) {
                  right = graphObject.getX() + graphObject.getWidth();
               }
               if (graphObject.getY() < top) {
                  top = graphObject.getY();
               } 
               if (graphObject.getY() + graphObject.getHeight() > bottom) {
                  bottom = graphObject.getY() + graphObject.getHeight(); 
               }
               nodesToPaste.add(graphObject.copy());
            }
         }
      }

      if (nodesToPaste.isEmpty() == false) {
         objectsToPaste.clear(); 
         pasteRectangle.setRect(left, top, right - left, bottom - top);
         origin.setLocation(ZoomController.getUnzoomedValue(left, zoom), 
                            ZoomController.getUnzoomedValue(top, zoom));
         objectsToPaste.add(nodesToPaste);
         objectsToPaste.add(arcsToPaste);
      }
   }

   
   public void showPasteRectangle(GuiView view) {
      if (!pasteInProgress) {
         view.add(this);
         requestFocusInWindow();
         try {
            if (zoom != view.getZoom()){
               updateSize(pasteRectangle, zoom, view.getZoom());
               zoom = view.getZoom();
            }
            pasteRectangle.setLocation(view.getMousePosition());
         } catch (java.lang.NullPointerException npe){
//        	npe.printStackTrace();
//            System.out.println(npe);
         }
         view.setLayer(this, Constants.SELECTION_LAYER_OFFSET);
         repaint();
         pasteInProgress = true;
         updateBounds();
      }
   }


   
   private void doPaste(GuiView view){
      ArrayList <UndoableEdit> undo = new ArrayList();
      
      pasteInProgress = false;
      view.remove(this);

      double despX = Grid.getModifiedX(
              ZoomController.getUnzoomedValue(pasteRectangle.getX(), zoom) - origin.getX());
      double despY = Grid.getModifiedY(
              ZoomController.getUnzoomedValue(pasteRectangle.getY(), zoom) - origin.getY());
      
      if (objectsToPaste.isEmpty()) {
         return;
      }
      
      UndoManager undoManager = view.getUndoManager();
      GraphDataLayerInterface model = CreateGraphGui.getModel();
      
      //First, we deal with nodes & Annotations
      ArrayList <GraphObject>nodesToPaste = objectsToPaste.get(0);
      for (int i = 0; i < nodesToPaste.size(); i++) {
         GraphObject pnObject = (GraphObject)
                 nodesToPaste.get(i).paste(despX, despY, sourceView != view, model);
         
         if (pnObject != null) {
            model.addGraphObject(pnObject);
            view.addNewGraphObject(pnObject);
            view.updatePreferredSize();
            pnObject.select();      
            undo.add(new AddGraphObjectEdit(pnObject, view, model));
         }
      }
      
      //Now, we deal with Arcs
      ArrayList <GraphArc> arcsToPaste = objectsToPaste.get(1);      
      for (int i = 0; i < arcsToPaste.size(); i++) {
         if (!(arcsToPaste.get(i) instanceof GraphArc)) {
            break;
         }
         GraphArc arc = (GraphArc)(arcsToPaste.get(i)).paste(
                 despX, despY, sourceView != view, model);
         if (arc != null) {
            model.addGraphObject(arc);
            view.addNewGraphObject(arc);
            view.updatePreferredSize();
            arc.select();
            arc.updateArcPosition();
            undo.add(new AddGraphObjectEdit(arc, view, model));
         }
      }
      
      // Clear copies
      nodesToPaste = objectsToPaste.get(0);
      for (GraphObject node : nodesToPaste) {
         if (node instanceof GraphAbstractNode) {
            if (((GraphAbstractNode)node).getOriginal() != null){
               //the Place/Transition is a copy of another Object, so we have to
               // nullify the reference to the original Object
               ((GraphAbstractNode)node).getOriginal().resetLastCopy();
            } else {
               ((GraphAbstractNode)node).resetLastCopy();
            }               
         }
      }
      
      // Add undo edits
      undoManager.newEdit(); // new "transaction""
      
      Iterator <UndoableEdit> undoIterator = undo.iterator();
      while (undoIterator.hasNext()){
         undoManager.addEdit(undoIterator.next());
      }
      
      view.zoom(); //
   }

  
   public void cancelPaste() {
      cancelPaste(CreateGraphGui.getView());
   }
  
      
   public void cancelPaste(GuiView view) {         
      pasteInProgress = false;
      view.repaint();
      view.remove(this);   
   }
   
   
   public boolean pasteInProgress() {
      return pasteInProgress;
   }
   
   
   public boolean pasteEnabled() {
      return !objectsToPaste.isEmpty();
   }
   
   
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setPaint(PASTE_COLOR);
      g2d.fill(pasteRectangle);
      g2d.setXORMode(PASTE_COLOR_OUTLINE);
      g2d.draw(pasteRectangle);
   }
   

   public void zoomUpdate(int newZoom) {
      updateSize(pasteRectangle, zoom, newZoom);
      zoom = newZoom;
   }

   
   private void updateSize(Rectangle pasteRectangle, int zoom, int newZoom) {
      int realWidth = ZoomController.getUnzoomedValue(pasteRectangle.width, zoom);
      int realHeight = ZoomController.getUnzoomedValue(pasteRectangle.height, zoom);
      
      pasteRectangle.setSize((int)(realWidth* ZoomController.getScaleFactor(newZoom)),
                             (int)(realHeight* ZoomController.getScaleFactor(newZoom)));
   }
      
   
   /* (non-Javadoc)
    * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
    */
   public void mousePressed(MouseEvent e) {
      ; // Not needed
   }

   
   /* (non-Javadoc)
    * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
    */
   public void mouseReleased(MouseEvent e) {
      ; // Not needed
   }

   
   /* (non-Javadoc)
    * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
    */
   public void mouseDragged(MouseEvent e) {
      if (pasteInProgress){
         updateRect(e.getPoint());
      }
   }

   
   /* (non-Javadoc)
    * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
    */
   public void mouseMoved(MouseEvent e) {
      if (pasteInProgress){
         updateRect(e.getPoint());
      }
   }
   
   
   private void updateRect(Point point){
      pasteRectangle.setLocation(point);
      //view.updatePreferredSize();
      repaint();
      updateBounds();
   }
   
      
   public void mouseClicked(MouseEvent e) {
      GuiView view = CreateGraphGui.getView();
      
      view.updatePreferredSize();
      view.setLayer(this, Constants.LOWEST_LAYER_OFFSET);
      repaint();
      //now, we have the position of the pasted objects so we can show them.
      doPaste(view);       
   }

   
   public void mouseEntered(MouseEvent e) {
      ; // Not needed
   }

   
   public void mouseExited(MouseEvent e) {
      ; // Not needed
   }   
   
   
   public void keyTyped(KeyEvent e) {
      ; // Not needed
   }

   
   public void keyPressed(KeyEvent e) {
      ; // Not needed
   }

   
   public void keyReleased(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
         cancelPaste();
      }
   }

}
