package pige.gui.handler;

import java.awt.Container;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import pige.dataLayer.GraphDataLayerInterface;
import pige.dataLayer.GraphAbstractNode;
import pige.dataLayer.GraphArc;
import pige.dataLayer.GraphNode;
import pige.gui.Constants;
import pige.gui.CreateGraphGui;
import pige.gui.GraphPanel;
import pige.gui.GuiView;
import pige.gui.undo.AddGraphObjectEdit;
import pige.gui.undo.UndoManager;

/**
 * @author Pere Bonet
 * @author Matthew Worthington
 */
public class GraphAbstractNodeHandler 
        extends GraphObjectHandler {
   
   ArcKeyboardEventHandler keyHandler = null;
   
   // constructor passing in all required objects
   public GraphAbstractNodeHandler(Container contentpane,
           GraphAbstractNode obj) {
      super(contentpane, obj);
      enablePopup = true;
   }
   
   
   private void createArc(GraphArc newArc, GraphAbstractNode currentObject){
	   newArc.setZoom(CreateGraphGui.getView().getZoom());
      contentPane.add(newArc);
      currentObject.addConnectFrom(newArc);
      CreateGraphGui.getView().createArc = (GraphArc)newArc;
      // add object a handler for shift & esc actions drawing arc
      // this is removed when the arc is finished drawing:
      keyHandler = new ArcKeyboardEventHandler((GraphArc)newArc);
      newArc.addKeyListener(keyHandler);
      newArc.requestFocusInWindow();
      newArc.setSelectable(false);
   }
   
   
   public void mousePressed(MouseEvent e) {
      super.mousePressed(e);
      // Prevent creating arcs with a right-click or a middle-click
      if (e.getButton() != MouseEvent.BUTTON1) {
         return;
      }
      
      GraphAbstractNode currentObject = (GraphAbstractNode)myObject;
      switch (CreateGraphGui.getGraphPanel().getMode()) {
         case Constants.GRAPHARC:
            if (e.isControlDown()) {
               // user is holding Ctrl key; switch to fast mode
               if (this.myObject instanceof GraphNode) {
                  CreateGraphGui.getGraphPanel().enterFastMode(Constants.FAST_TRANSITION);
               } 
            }
         case Constants.FAST_PLACE:
            if (CreateGraphGui.getView().createArc == null) {
                  createArc(new GraphArc(currentObject), currentObject);
            }
            break;
            
         default:
            break;
      }
   }
   
   public boolean isValidArcType(GraphAbstractNode currentObject, GraphArc createArc){
		return currentObject != createArc.getSource() || 
		   (CreateGraphGui.graphType.allowsSelfArc() && createArc.getArcPath().getNumPoints()>=Constants.MIN_SELFARC_POINTS);
   }
   
   public void mouseReleased(MouseEvent e) {
      boolean isNewArc = true; // true if we have to add a new arc
      boolean fastMode = false;
      
      GuiView view = CreateGraphGui.getView();
      GraphDataLayerInterface model = CreateGraphGui.getModel();
      UndoManager undoManager = view.getUndoManager();
      GraphPanel app = CreateGraphGui.getGraphPanel();
      
      super.mouseReleased(e);
      
      GraphAbstractNode currentObject = (GraphAbstractNode)myObject;
      
      switch (app.getMode()) {
         case Constants.FAST_PLACE:
            fastMode = true;
         case Constants.GRAPHARC:
            GraphArc createArc = (GraphArc) view.createArc;
            if (createArc != null) {
               if (isValidArcType(currentObject, createArc)) {
                  createArc.setSelectable(true);
                  Iterator arcsFrom = createArc.getSource().getConnectFromIterator();
                  // search for pre-existent arcs from createArc's source to 
                  // createArc's target                  
                  while(arcsFrom.hasNext()) {
                     GraphArc someArc = ((GraphArc)arcsFrom.next());
                     if (someArc == createArc) {
                        break;
                     } else if (someArc.getSource() == createArc.getSource() &&
                             someArc.getTarget() == currentObject) {
                    	if (!CreateGraphGui.graphType.allowsMultiplefArcs()){
                    		isNewArc = false;
                    		createArc.delete();
                    	}
                        break; 
                     }
                  }
                  
                  if (isNewArc == true) {
                     currentObject.addConnectTo(createArc);
                     
                     // Evil hack to prevent the arc being added to GuiView twice
                     contentPane.remove(createArc);
                     
                     model.addArc((GraphArc)createArc);
                     view.addNewGraphObject(createArc);
                     if (!fastMode) {
                        // we are not in fast mode so we have to set a new edit
                        // in undoManager for adding the new arc
                        undoManager.newEdit(); // new "transaction""
                     }
                     undoManager.addEdit(
                             new AddGraphObjectEdit(createArc, view, model));
                  }
                  
                  // arc is drawn, remove handler:
                  createArc.removeKeyListener(keyHandler);
                  keyHandler = null;
                  if (isNewArc == false){
                     view.remove(createArc);
                  }
                  view.createArc = null;
               }
            }
            
            if (app.getMode() == Constants.FAST_PLACE ||
                    app.getMode() == Constants.FAST_TRANSITION) {
               if (view.newGO == true) {
                  // a new PNO has been created 
                  view.newGO = false;
                  if (currentObject instanceof GraphNode) {
                     app.setMode(Constants.FAST_TRANSITION);
                  }
               } else {
                  if (view.createArc == null) {
                     app.resetMode();
                  } else {
                	  app.setMode(Constants.FAST_PLACE);
                  }
               }
            }
            break;
            
         default:
            break;
      }
   }
   
}
