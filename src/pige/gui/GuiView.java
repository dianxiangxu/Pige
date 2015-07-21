package pige.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLayeredPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import pige.dataLayer.GraphDataLayerInterface;
import pige.dataLayer.GraphAbstractNode;
import pige.dataLayer.GraphAnnotationNote;
import pige.dataLayer.GraphArc;
import pige.dataLayer.GraphNode;
import pige.dataLayer.GraphNodeFactory;
import pige.dataLayer.GraphObject;
import pige.dataLayer.Note;
import pige.gui.handler.AnnotationNoteHandler;
import pige.gui.handler.ArcHandler;
import pige.gui.handler.GraphNodeHandler;
import pige.gui.handler.LabelHandler;
import pige.gui.undo.AddGraphObjectEdit;
import pige.gui.undo.UndoManager;


/**
 * The graph is drawn onto this frame.
 */
public class GuiView 
        extends JLayeredPane 
        implements Observer, Printable {
   
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public boolean graphChanged = false;
      
   public GraphArc createArc;  //no longer static
   
   public GraphAbstractNode createPTO;
   
   // When i'm using GNU/Linux, isMetaDown() doesn't return true when I press 
   // "Windows key". I don't know if a problem of my configuration or what.
   // metaDown is used in this case
   boolean metaDown = false; 
   
   private SelectionManager selection;
   
   private UndoManager undoManager;
   
   private GraphDataLayerInterface model;
   
   private ArrayList <GraphObject> graphObjects = new ArrayList<GraphObject>();
   
   private GraphPanel app;
   
   private ZoomController zoomControl;
   
   // flag used in fast mode to know if a new GraphObject has been created
   public boolean newGO = false;

   // flag used in paintComponents() to know if a call to zoom() has been done
   private boolean doSetViewPosition = true;

   // position where the viewport must be set
   private Point viewPosition = new Point(0,0);
   
   
   public GuiView(GraphDataLayerInterface _model, GraphPanel _app) {
      model = _model;
      app = _app;
      
      setLayout(null);
      setOpaque(true);
      setDoubleBuffered(true);
      setAutoscrolls(true);
      setBackground(Constants.ELEMENT_FILL_COLOUR);
      
      zoomControl = new ZoomController(100);
      
      MouseHandler handler = new MouseHandler(this, model);
      
      setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      
      addMouseListener(handler);
      addMouseMotionListener(handler);
      try {
         addMouseWheelListener((MouseWheelListener)handler);
      } catch (Exception e) {
         e.printStackTrace();
      }
      
      selection = new SelectionManager(this);
      undoManager = new UndoManager(this, model, app);
   }
   
   
   public void addNewGraphObject(GraphObject newObject) {
      if (newObject != null) {
         if (newObject.getMouseListeners().length == 0) {
            if (newObject instanceof GraphNode) {
               LabelHandler labelHandler =
                       new LabelHandler(((GraphNode)newObject).getNameLabel(),
                       (GraphNode)newObject);
               ((GraphNode)newObject).getNameLabel().addMouseListener(labelHandler);
               ((GraphNode)newObject).getNameLabel().addMouseMotionListener(labelHandler);
               ((GraphNode)newObject).getNameLabel().addMouseWheelListener(labelHandler);
               
               GraphNodeHandler nodeHandler =
                       new GraphNodeHandler(this, (GraphNode)newObject);
               newObject.addMouseListener(nodeHandler);
               newObject.addMouseWheelListener(nodeHandler);
               newObject.addMouseMotionListener(nodeHandler);
               add(newObject);
            } else if (newObject instanceof GraphArc) {
            	add(newObject);
               ArcHandler arcHandler = new ArcHandler(this, (GraphArc)newObject);
               newObject.addMouseListener(arcHandler);
               newObject.addMouseWheelListener(arcHandler);
               newObject.addMouseMotionListener(arcHandler);
            } else if (newObject instanceof GraphAnnotationNote) {
               add(newObject);
               AnnotationNoteHandler noteHandler =
                       new AnnotationNoteHandler(this, (GraphAnnotationNote)newObject);
               newObject.addMouseListener(noteHandler);
               newObject.addMouseMotionListener(noteHandler);
               ((Note)newObject).getNote().addMouseListener(noteHandler);
               ((Note)newObject).getNote().addMouseMotionListener(noteHandler);
            }
            newObject.zoomUpdate(getZoom());
         }
      }
      validate();
      repaint();
   }
   
   
   public void update(Observable o, Object diffObj) {
      if ((diffObj instanceof GraphObject) && (diffObj != null)) {
//         if (CreateGui.appGui.getMode() == Constants.CREATING) {
            
            addNewGraphObject((GraphObject)diffObj);
//         }
         repaint();
      }
   }
   
   
   public int print(Graphics g, PageFormat pageFormat, int pageIndex)
   throws PrinterException {
      if (pageIndex > 0) {
         return Printable.NO_SUCH_PAGE;
      }
      Graphics2D g2D = (Graphics2D) g;
      //Move origin to page printing area corner
      g2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      g2D.scale(0.5,0.5);
      print(g2D); // Draw the net
      
      return Printable.PAGE_EXISTS;
   }
   
   
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (Grid.isEnabled()) {
         Grid.updateSize(this);
         Grid.drawGrid(g);
      }

      selection.updateBounds();

      if (doSetViewPosition) {
// Dianxiang Xu, the following statement is commented on 1/05/2012 
// because of stack overflow when zooming    	  
//    	       ((JViewport)getParent()).setViewPosition(viewPosition);         
//         ((JViewport)getParent()).setViewPosition(viewPosition);         
         app.validate();
         doSetViewPosition = false;
      }      
   }
   
   
   public void updatePreferredSize() {
      // iterate over net objects
      // setPreferredSize() accordingly

      Component[] components = getComponents();
      Dimension d = new Dimension(0,0);
      for (int i = 0; i < components.length; i++) {
         if (components[i].getClass() == SelectionManager.class) {
            continue; // SelectionObject not included
         }
         Rectangle r = components[i].getBounds();
         int x = r.x + r.width + 20;
         int y = r.y + r.height + 20;
         if (x > d.width) {
            d.width = x;
         }
         if (y > d.height) {
            d.height = y;
         }
      }
      setPreferredSize(d);
      Container parent = getParent();
      if (parent != null) {
         parent.validate();
      }
   }
   
/*   
   public void changeAnimationMode(boolean status) {
      animationmode = status;
   }
*/   
   
   public void setCursorType(String type) {
      if (type.equals("arrow")) {
         setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      } else if (type.equals("crosshair")) {
         setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      } else if (type.equals("move")) {
         setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
      }
   }
   
   
   public SelectionManager getSelectionObject() {
      return selection;
   }
   
   
   public UndoManager getUndoManager() {
      return undoManager;
   }
   
   
   public ZoomController getZoomController() {
      return zoomControl;
   }
   
   
   public void zoom() {
      Component[] children = getComponents();
      
      for (int i = 0; i < children.length; i++) {
         if (children[i] instanceof Zoomable) {
            ((Zoomable)children[i]).zoomUpdate(zoomControl.getPercent());
         }
      }
      doSetViewPosition = true;
   }
   
   
   public void add(GraphObject graphObject) {
      setLayer(graphObject, DEFAULT_LAYER.intValue() + graphObject.getLayerOffset());
      super.add(graphObject);
      graphObject.addedToGui();
      graphObjects.add(graphObject);
   }
   
   
   //
   public void setMetaDown(boolean down) {
      metaDown = down;
   }
   
   public boolean getGraphChanged() {
      return graphChanged;
   }
   
   
   public void setGraphChanged(boolean _graphChanged) {
      graphChanged = _graphChanged;
   }
   
   
   public ArrayList <GraphObject> getGraphObjects() {
      return graphObjects;
   }
   
   
   public void remove(Component comp) {
      graphObjects.remove(comp);
      //if (result) {
      //   System.out.println("DEBUG: remove PNO from view");
      ///}
      super.remove(comp);
   }
   
   
   public void drag(Point dragStart, Point dragEnd) {
      if (dragStart == null) {
         return;
      }
      JViewport viewer = (JViewport)getParent();
      Point offScreen = viewer.getViewPosition();
      if (dragStart.x > dragEnd.x){
         offScreen.translate(viewer.getWidth(), 0);
      }
      if (dragStart.y > dragEnd.y){
         offScreen.translate(0, viewer.getHeight());
      }
      offScreen.translate(dragStart.x - dragEnd.x, dragStart.y - dragEnd.y);
      Rectangle r = new Rectangle(offScreen.x, offScreen.y, 1, 1);
      scrollRectToVisible(r);
   }
   
   
   private Point midpoint(int zoom){
      JViewport viewport = (JViewport)getParent();
      double midpointX = ZoomController.getUnzoomedValue(
              viewport.getViewPosition().x + (viewport.getWidth() * 0.5), zoom);
      double midpointY = ZoomController.getUnzoomedValue(
              viewport.getViewPosition().y + (viewport.getHeight() * 0.5), zoom);
      return (new java.awt.Point((int)midpointX, (int)midpointY));
   }
   
   
   public void zoomIn(){
      int zoom = zoomControl.getPercent();
      if (zoomControl.zoomIn()) {
         zoomTo(midpoint(zoom));
      }
   }
   
   
   public void zoomOut(){
      int zoom = zoomControl.getPercent();
      if (zoomControl.zoomOut()) {
         zoomTo(midpoint(zoom));
      }
   }
   
   
   public void zoomTo(Point point){
      // The zoom is not as smooth as it should be. As far I know, this behavior
      // is caused when the method setSize() is called in NameLabel's updateSize()
      // In order to disguise it, the view is hidden and a white layer is shown.
      // I know it's not a smart solution...
      // I think zoom function should be redone from scratch so that BlankLayer
      // class and doSetViewPosition could be removed
      

      int zoom = zoomControl.getPercent();
       
      JViewport viewport = (JViewport)getParent();
      
//      double currentXNoZoom = ZoomController.getUnzoomedValue(
//              viewport.getViewPosition().x + (viewport.getWidth() * 0.5), zoom);

      double newZoomedX = ZoomController.getZoomedValue(point.x, zoom);
      double newZoomedY = ZoomController.getZoomedValue(point.y, zoom);
      
      int newViewX = (int)(newZoomedX - (viewport.getWidth() * 0.5));
      if (newViewX < 0) {
         newViewX = 0;
      }
      
      int newViewY = (int)(newZoomedY - (viewport.getHeight() * 0.5));
      if (newViewY < 0) {
         newViewY = 0;
      }

      //if (doSetViewPosition) {
         viewPosition.setLocation(newViewX, newViewY);
         viewport.setViewPosition(viewPosition);
      //}
      
      zoom();
      
      app.hideNet(true); // hide current view :-(

      updatePreferredSize();            
   }
   
   
   public int getZoom() {
      return zoomControl.getPercent();
   }
   
   class MouseHandler extends MouseInputAdapter {
      
      private GraphObject graphObject;
      
      private GuiView view;
      
      private GraphDataLayerInterface model;
      
      private Point dragStart;
      
      
      public MouseHandler(GuiView _view, GraphDataLayerInterface _model){
         super();
         view = _view;
         model = _model;
      }
      
      
      private Point adjustPoint(Point p, int zoom) {
         int offset = (int)(ZoomController.getScaleFactor(zoom) *
                 Constants.GRAPHNODE_HEIGHT/2);
         
         int x = ZoomController.getUnzoomedValue(p.x - offset, zoom);
         int y = ZoomController.getUnzoomedValue(p.y - offset, zoom);
         
         p.setLocation(x, y);
         return p;
      }
      
      
      private GraphAbstractNode newGraphNode(Point p){
         p = adjustPoint(p, view.getZoom());
         
         graphObject = GraphNodeFactory.createGraphNode(Grid.getModifiedX(p.x), Grid.getModifiedY(p.y));
         model.addGraphObject(graphObject);
         view.addNewGraphObject(graphObject);
         return (GraphAbstractNode)graphObject;
      }
      
      
      public void mousePressed(MouseEvent e){
         Point start = e.getPoint();
         Point p;
         
         if (SwingUtilities.isLeftMouseButton(e)) {
            int mode = app.getMode();
            switch (mode){
               case Constants.GRAPHNODE:
                  GraphAbstractNode pto = newGraphNode(e.getPoint());
                  getUndoManager().addNewEdit(
                          new AddGraphObjectEdit(pto, view, model));
                  if (e.isControlDown()) {
                     app.enterFastMode(Constants.FAST_TRANSITION);
                     graphObject.dispatchEvent(e);
                  }
                  break;
                  
               case Constants.GRAPHARC:
                  // Add point to arc in creation
                  if (createArc != null) {
                     addPoint(createArc, e);
                  }
                  break;
                  
               case Constants.GRAPHANNOTATION:
                  p = adjustPoint(e.getPoint(), view.getZoom());
                  
                  graphObject = new GraphAnnotationNote(p.x, p.y);
                  model.addGraphObject(graphObject);
                  view.addNewGraphObject(graphObject);
                  getUndoManager().addNewEdit(
                          new AddGraphObjectEdit(graphObject, view, model));
                  ((GraphAnnotationNote)graphObject).enableEditMode();
                  break;
                  
               case Constants.FAST_PLACE:
                  if (e.isMetaDown() || metaDown) { // provisional
                     if (createArc != null) {
                        addPoint(createArc, e);
                     }
                  } else {
                     if (createArc == null) {
                        break;
                     }                     
                     view.newGO = true;

                     createPTO = newGraphNode(e.getPoint());
                     getUndoManager().addNewEdit(
                             new AddGraphObjectEdit(createPTO, view, model));
                     graphObject.getMouseListeners()[0].mouseReleased(e);
                     if (e.isControlDown()){
                        // keep "fast mode"
                        app.setMode(Constants.FAST_TRANSITION);
                        graphObject.getMouseListeners()[0].mousePressed(e);
                     } else {
                        //exit "fast mode"
                        app.resetMode();
                     }
                  }
                  break;
                  
               case Constants.DRAG:
                  dragStart = new Point(start);
                  break;
                  
               default:
                  break;
            }
         } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            dragStart = new Point(start);
         }
         updatePreferredSize();
      }
      
      
      private void addPoint(final GraphArc createArc, final MouseEvent e) {
         int x = Grid.getModifiedX(e.getX());
         int y = Grid.getModifiedY(e.getY());
         boolean shiftDown = e.isShiftDown();
         createArc.setEndPoint(x, y, shiftDown);
         createArc.getArcPath().addPoint(x, y, shiftDown);
      }
      
      
      public void mouseReleased(MouseEvent e){
         setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      }
      
      
      public void mouseMoved(MouseEvent e) {
         if (createArc != null) {
            createArc.setEndPoint(Grid.getModifiedX(e.getX()),
                    Grid.getModifiedY(e.getY()),
                    e.isShiftDown());
         }
      }
      
      
      /**
       * @see javax.swing.event.MouseInputAdapter#mouseDragged(java.awt.event.MouseEvent)
       */
      public void mouseDragged(MouseEvent e) {
         view.drag(dragStart, e.getPoint());
      }
      
      
      public void mouseWheelMoved(MouseWheelEvent e) {
         if (!e.isControlDown()) {
            return;
         } else {
            if (e.getWheelRotation()> 0) {
               view.zoomIn();
            } else {
               view.zoomOut();
            }
         }
      }
      
   }
   
}
