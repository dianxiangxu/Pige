/*
 * Created on 04-Mar-2004
 * Author is Michael Camacho
 */
package pige.dataLayer;
   
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import pige.gui.Constants;
import pige.gui.CreateGraphGui;
import pige.gui.Grid;
import pige.gui.ZoomController;
import pige.gui.undo.AnnotationTextEdit;
import pige.gui.widgets.AnnotationPanel;
import pige.gui.widgets.EscapableDialog;


public class GraphAnnotationNote 
        extends Note {

   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

private boolean fillNote = true;
   
   private ResizePoint[] dragPoints = new ResizePoint[8];
   
   private AffineTransform prova = new AffineTransform();
   
   
   public GraphAnnotationNote(int x, int y) {
      super(x,y);
      setDragPoints();
		if (CreateGraphGui.getGraphPanel() != null)
		{
			this.addZoomController(CreateGraphGui.getView().getZoomController());
		}
   }

   
   public GraphAnnotationNote(String id, String text, int x, int y) {
      super (id, text, x, y);
      setDragPoints();
		if (CreateGraphGui.getGraphPanel() != null)
		{
			this.addZoomController(CreateGraphGui.getView().getZoomController());
		}
   }

   
   public GraphAnnotationNote(String text, int x, int y, int w, int h, boolean border) {
      super (text, x, y, w, h, border);
      setDragPoints();
		if (CreateGraphGui.getGraphPanel() != null)
		{
			this.addZoomController(CreateGraphGui.getView().getZoomController());
		}
   }
   

   private void setDragPoints() {
      dragPoints[0] = new ResizePoint(this, ResizePoint.TOP | 
                                            ResizePoint.LEFT);
      dragPoints[1] = new ResizePoint(this, ResizePoint.TOP);
      dragPoints[2] = new ResizePoint(this, ResizePoint.TOP |
                                            ResizePoint.RIGHT);
      dragPoints[3] = new ResizePoint(this, ResizePoint.RIGHT);
      dragPoints[4] = new ResizePoint(this, ResizePoint.BOTTOM | 
                                            ResizePoint.RIGHT);;
      dragPoints[5] = new ResizePoint(this, ResizePoint.BOTTOM);
      dragPoints[6] = new ResizePoint(this, ResizePoint.BOTTOM | 
                                            ResizePoint.LEFT);
      dragPoints[7] = new ResizePoint(this, ResizePoint.LEFT);
      
      for (int i = 0; i < 8; i++) {
         ResizePointHandler handler = new ResizePointHandler(dragPoints[i]);
         dragPoints[i].addMouseListener(handler);
         dragPoints[i].addMouseMotionListener(handler);
         add(dragPoints[i]);
      }            
   }
   

   public void updateBounds() {
      super.updateBounds();
      if (dragPoints != null) {
         // TOP-LEFT
         dragPoints[0].setLocation(
                 ZoomController.getZoomedValue(noteRect.getMinX() , zoom),
                 ZoomController.getZoomedValue(noteRect.getMinY(), zoom));
         dragPoints[0].setZoom(zoom);
         // TOP-MIDDLE 
         dragPoints[1].setLocation( 
                 ZoomController.getZoomedValue(noteRect.getCenterX(), zoom),
                 ZoomController.getZoomedValue(noteRect.getMinY(), zoom));
         dragPoints[1].setZoom(zoom);
         // TOP-RIGHT
         dragPoints[2].setLocation( 
                 ZoomController.getZoomedValue(noteRect.getMaxX(), zoom),
                 ZoomController.getZoomedValue(noteRect.getMinY(), zoom));
         dragPoints[2].setZoom(zoom);
         // MIDDLE-RIGHT
         dragPoints[3].setLocation( 
                 ZoomController.getZoomedValue(noteRect.getMaxX(), zoom),
                 ZoomController.getZoomedValue(noteRect.getCenterY(), zoom));
         dragPoints[3].setZoom(zoom);
         // BOTTOM-RIGHT
         dragPoints[4].setLocation( 
                 ZoomController.getZoomedValue(noteRect.getMaxX(), zoom),
                 ZoomController.getZoomedValue(noteRect.getMaxY(), zoom));
         dragPoints[4].setZoom(zoom);
         // BOTTOM-MIDDLE
         dragPoints[5].setLocation( 
                 ZoomController.getZoomedValue(noteRect.getCenterX(), zoom),
                 ZoomController.getZoomedValue(noteRect.getMaxY(), zoom));    
         dragPoints[5].setZoom(zoom);
         // BOTTOM-LEFT
         dragPoints[6].setLocation( 
                 ZoomController.getZoomedValue(noteRect.getMinX(), zoom),
                 ZoomController.getZoomedValue(noteRect.getMaxY(), zoom));                     
         dragPoints[6].setZoom(zoom);
         // MIDDLE-LEFT
         dragPoints[7].setLocation( 
                 ZoomController.getZoomedValue(noteRect.getMinX(), zoom),
                 ZoomController.getZoomedValue(noteRect.getCenterY(), zoom));
         dragPoints[7].setZoom(zoom);
      }
   }   
   
   
   public boolean contains(int x, int y) {
      boolean pointContains = false;
      
      for (int i = 0; i < 8; i++) {
         pointContains |= dragPoints[i].contains(x - dragPoints[i].getX(), 
                                                 y - dragPoints[i].getY());
      }
      
      return super.contains(x, y) || pointContains;
   }
   
   
   
   public void enableEditMode() {
      String oldText = note.getText();  

      // Build interface
      EscapableDialog guiDialog =
              new EscapableDialog(CreateGraphGui.getParentFrame(), "PIGE", true);

      guiDialog.add(new AnnotationPanel(this));

      // Make window fit contents' preferred size
      guiDialog.pack();
      
      // Move window to the middle of the screen
      guiDialog.setLocationRelativeTo(null);
      
      guiDialog.setResizable(false);
      guiDialog.setVisible(true);
      
      guiDialog.dispose();  
      
      String newText = note.getText();
      if (oldText != null && !newText.equals(oldText)) {
         // Text has been changed
         CreateGraphGui.getView().getUndoManager().addNewEdit(
                 new AnnotationTextEdit(this, oldText, newText));
         updateBounds();
      }      
   }

   
   public GraphAnnotationNote paste(double x, double y, boolean toAnotherView, GraphDataLayerInterface model) {
      return new GraphAnnotationNote (this.note.getText(),   
                                 Grid.getModifiedX(x + this.getX()),
                                 Grid.getModifiedY(y + this.getY()),
                                 this.note.getWidth(),
                                 this.note.getHeight(),
                                 this.isShowingBorder());
   }   

   
   public GraphAnnotationNote copy() {
      return new GraphAnnotationNote (this.note.getText(),  
                                 ZoomController.getUnzoomedValue(this.getX(), zoom),
                                 ZoomController.getUnzoomedValue(this.getY(), zoom),
                                 this.note.getWidth(),
                                 this.note.getHeight(),
                                 this.isShowingBorder());
   }         

   
   public void paintComponent(Graphics g) {
      super.paintComponent(g);      
      
      Graphics2D g2 = (Graphics2D)g;
      prova = g2.getTransform();   
      
      g2.setStroke(new BasicStroke(1.0f));
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                          RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, 
                          RenderingHints.VALUE_STROKE_NORMALIZE);

      g2.transform(ZoomController.getTransform(zoom));   
      
   	 Color highlightColor = getHighlightTextColor();
   	 if (highlightColor!=null)
   		 note.setDisabledTextColor(highlightColor);
   	 else
   		 note.setDisabledTextColor(Constants.NOTE_DISABLED_COLOUR);

      if (selected && !ignoreSelection) {
         g2.setPaint(Constants.SELECTION_FILL_COLOUR);
         g2.fill(noteRect);
         if (drawBorder) {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
            g2.draw(noteRect);
         }
      } else {
         g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
         if (fillNote) {
            g2.fill(noteRect);
         }
         if (drawBorder) {
        	 if (highlightColor!=null)
        		 g2.setPaint(Color.ORANGE);
        	 else 
        		 g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
            g2.draw(noteRect);
         }
      }
      for (int i = 0; i < 8; i++) {
         dragPoints[i].myPaintComponent(g);
      }          
      
      g2.transform(ZoomController.getTransform(zoom));   
   }

	private static final String INIT_KEYWORD = "INIT";
	private static final String GOAL_KEYWORD = "GOAL";

 private Color getHighlightTextColor(){
	   String text = note.getText();
	   if (text.startsWith(INIT_KEYWORD ))
		   return Color.BLUE;
	   else
	   if (text.startsWith(GOAL_KEYWORD))
		   return new Color(16,0,192);
	   else
	   return null;
  }

   public int getLayerOffset() {
      return Constants.NOTE_LAYER_OFFSET;
   }

   
   public boolean isFilled() {
	return fillNote;
   }
   
   
   public void changeBackground() {
      fillNote = !fillNote;
      note.setOpaque(fillNote);
   } 
   
   

   private class ResizePointHandler 
           extends javax.swing.event.MouseInputAdapter {
      
      private ResizePoint myPoint;
      private Point start;
      
      
      public ResizePointHandler(ResizePoint point) {
         myPoint = point;
      }
      
      
      public void mousePressed(MouseEvent e) {
         myPoint.myNote.setDraggable(false);
         myPoint.isPressed = true;
         myPoint.repaint();
         start = e.getPoint();
      }

      
      public void mouseDragged(MouseEvent e) {
         myPoint.drag(Grid.getModifiedX(e.getX() - start.x), 
                      Grid.getModifiedY(e.getY() - start.y));
         myPoint.myNote.updateBounds();
         myPoint.repaint();
      }

      
      public void mouseReleased(MouseEvent e) {
         myPoint.myNote.setDraggable(true);
         myPoint.isPressed = false;
         myPoint.myNote.updateBounds();
         myPoint.repaint();
      }
      
   }

    
   
   public class ResizePoint 
           extends javax.swing.JComponent {
 
      /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int SIZE   = 3;
      private static final int TOP    = 1;
      private static final int BOTTOM = 2;
      private static final int LEFT   = 4;
      private static final int RIGHT  = 8;
      
      private Rectangle shape;
      private boolean isPressed = false;
      private Note myNote;
      public int typeMask;
     
      
      public ResizePoint(Note obj, int type) {
         myNote = obj;
         setOpaque(false);
         setBounds(-SIZE -1,
                   -SIZE - 1, 
                   2*SIZE + Constants.ANNOTATION_SIZE_OFFSET + 1,
                   2*SIZE + Constants.ANNOTATION_SIZE_OFFSET + 1);
         typeMask = type;
      }
      
      
      public void setLocation(double x, double y) {
         super.setLocation((int)(x - SIZE), (int)(y - SIZE));
      }
      
      
      private void drag(int x, int y) {
         if ((typeMask & TOP) == TOP) {
            myNote.adjustTop(ZoomController.getUnzoomedValue(y, zoom));
         }
         if ((typeMask & BOTTOM) == BOTTOM) {
            myNote.adjustBottom(ZoomController.getUnzoomedValue(y, zoom));
         }
         if ((typeMask & LEFT) == LEFT) {
            myNote.adjustLeft(ZoomController.getUnzoomedValue(x, zoom));
         }
         if ((typeMask & RIGHT) == RIGHT) {
            myNote.adjustRight(ZoomController.getUnzoomedValue(x, zoom));
         }
         CreateGraphGui.getView().setGraphChanged(true);
      }
      
      
      public void  myPaintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g;
         g2.setTransform(prova);
         if (myNote.selected && !GraphObject.ignoreSelection) {
            g2.translate(this.getLocation().x, this.getLocation().y);
            shape = new Rectangle(0, 0, 2 * SIZE, 2 * SIZE);
            g2.fill(shape);

            g2.setStroke(new BasicStroke(1.0f));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                RenderingHints.VALUE_ANTIALIAS_ON);
            if (isPressed) {
               g2.setPaint(Constants.RESIZE_POINT_DOWN_COLOUR);
            } else {
               g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
            }
            g2.fill(shape);
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
            g2.draw(shape);
            g2.setTransform(prova);
         }
      }

      
      // change ResizePoint's size a little bit acording to the zoom percent
      private void setZoom(int percent) {
         if (zoom >= 220) {
            SIZE = 5;
         } else if (zoom >= 120) {
            SIZE = 4;
         } else if (zoom >= 60) {
            SIZE = 3;
         }
      }
   }

}
