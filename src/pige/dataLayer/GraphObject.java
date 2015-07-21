package pige.dataLayer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.EventListener;

import javax.swing.JComponent;

import pige.gui.Constants;
import pige.gui.CopyPasteable;
import pige.gui.CreateGraphGui;
import pige.gui.GuiView;
import pige.gui.Translatable;
import pige.gui.ZoomController;
import pige.gui.Zoomable;
import pige.gui.undo.GraphObjectNameEdit;
import pige.gui.undo.UndoableEdit;


/**
 * @version 1.0
 * @author James D Bloom
 */
public abstract class GraphObject 
        extends JComponent 
        implements Zoomable, CopyPasteable, Cloneable, Translatable {

	private static final long serialVersionUID = 1L;

public static int COMPONENT_DRAW_OFFSET = 5;
   
   protected String id = "";
   protected NameLabel name;
   
	/** X-axis Position on screen */
	protected double nameOffsetX;
	/** Y-axis Position on screen */
	protected double nameOffsetY;

   protected Color objectColour = Constants.ELEMENT_LINE_COLOUR;
   protected Color selectionBorderColour = Constants.SELECTION_LINE_COLOUR;
   protected boolean selected = false;	// True if part of the current selection.
   protected boolean selectable = true;	// True if object can be selected.
   protected boolean draggable = true;	// True if object can be dragged.
   protected boolean copyPasteable  = true;	// True if object can be cloned.
   protected static boolean ignoreSelection = false;
   protected Rectangle bounds = new Rectangle();
   
   protected boolean deleted = false;
   protected boolean markedAsDeleted = false;
   
   // The ZoomController of the GuiView this component is part of.
   private ZoomController zoomControl;   
   
   // Integer value which represents a zoom percentage
   protected int zoom = 100;   
   
   public GraphObject(){
		name = new NameLabel(zoom);
   }

	public void addZoomController(final ZoomController zoomControl2){
		this.zoomControl = zoomControl2;
	}
   
   public void setId(String idInput) {
      id = idInput;
   }

   
   public String getId() {
      return id;
   }

   public NameLabel getNameLabel(){
      return name;
   }

   public void setNameLabel(NameLabel newNameLabel){
	      this.name = newNameLabel;
	   }

	public void setName(String nameInput) {
		if (name==null)
			name = new NameLabel(nameInput);
		else
		name.setName(nameInput);
	}

	public String getName() {
		return name.getName();
	}

	public Double getNameOffsetXObject() {
		return this.nameOffsetX;
	}

	public Double getNameOffsetYObject() {
		return this.nameOffsetY;
	}
	
	public void setNameOffsetX(double nameOffsetXInput) {
		nameOffsetX += ZoomController.getUnzoomedValue(nameOffsetXInput, zoom);
	}

	public void setNameOffsetY(double nameOffsetYInput) {
		nameOffsetY += ZoomController.getUnzoomedValue(nameOffsetYInput, zoom);
	}
   
   public void addLabelToContainer() {
      if (getParent() != null && name.getParent() == null) {
         getParent().add(name);
      }
   }
   
   public boolean isSelected() {
      return selected;
   }

   
   public void select() {
      if (selectable && !selected) {
         selected = true;
         repaint();
      }
   }

   
   public void deselect() {
      if (selected) {
         selected = false;
         repaint();
      }
   }

   
   public boolean isSelectable() {
      return selectable;
   }

   
   public void setSelectable(boolean allow) {
      selectable = allow;
   }

   
   public static void ignoreSelection(boolean ignore) {
      ignoreSelection = ignore;
   }

   
   public boolean isDraggable() {
      return draggable;
   }

   
   public void setDraggable(boolean allow) {
      draggable = allow;
   }

   
   public void setObjectColour(Color c) {
      objectColour = c;
   }

   
   public void setSelectionBorderColour(Color c) {
      selectionBorderColour = c;
   }

   
   public abstract void addedToGui();
   
   
   public void delete() {
      deleted = true;
      CreateGraphGui.getModel().removeGraphObject(this);
      removeFromContainer();
      removeAll();
   }
   
   
   public void undelete(GraphDataLayerInterface model, GuiView view) {
      model.addGraphObject(this);
      view.add(this);
   }
   
   
   protected void removeFromContainer() {
      Container c = getParent();
      
      if (c != null){
         c.remove(this);
      }
   }
   
   
   public UndoableEdit setGraphObjectName(String name){
      String oldName = this.getName();
      this.setName(name);
      return new GraphObjectNameEdit(this, oldName, name);               
   }
   
   
   public boolean isDeleted() {
      return deleted || markedAsDeleted;
   }

   
   public void markAsDeleted() {
      markedAsDeleted = true;
   }

   
   public void select(Rectangle selectionRectangle) {
      if (selectionRectangle.intersects(this.getBounds())) {
         select();
      }
   }


   public void paintComponent(Graphics g) {
      super.paintComponent(g);
   }
   
   
   public boolean isCopyPasteable() {
      return copyPasteable;
   }   

   public abstract int getLayerOffset();

   
   public int getZoom() {
      return zoom;
   }

   
   public GraphObject clone() {
      try {
         GraphObject graphObjectCopy = (GraphObject) super.clone();

         // Remove all mouse listeners on the new object
         EventListener[] mouseListeners = graphObjectCopy.getListeners(MouseListener.class);
         for (int i = 0; i < mouseListeners.length; i++){
            graphObjectCopy.removeMouseListener((MouseListener) mouseListeners[i]);
         }
         
         mouseListeners = graphObjectCopy.getListeners(MouseMotionListener.class);
         
         for (int i = 0; i < mouseListeners.length; i++) {
            graphObjectCopy.removeMouseMotionListener((MouseMotionListener) mouseListeners[i]);
         }
         
         mouseListeners = graphObjectCopy.getListeners(MouseWheelListener.class);
         
         for (int i = 0; i < mouseListeners.length; i++) {
            graphObjectCopy.removeMouseWheelListener((MouseWheelListener) mouseListeners[i]);
         }
         
         return graphObjectCopy;
      } catch (CloneNotSupportedException e) {
         throw new Error(e);
      }
   }


   public static int getComponentDrawOffset() {
	   return COMPONENT_DRAW_OFFSET;
   }

   public ZoomController getZoomController()
   {
	   return this.zoomControl;
   }
}
