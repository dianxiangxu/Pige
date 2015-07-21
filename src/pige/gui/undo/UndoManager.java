/*
 * UndoManager.java
 */
package pige.gui.undo;

import java.util.ArrayList;
import java.util.Iterator;

import pige.dataLayer.GraphDataLayerInterface;
import pige.dataLayer.GraphAbstractNode;
import pige.dataLayer.GraphArc;
import pige.dataLayer.GraphArcPathPoint;
import pige.dataLayer.GraphObject;
import pige.gui.Constants;
import pige.gui.GraphPanel;
import pige.gui.GuiView;


/**
 * Class to handle undo & redo functionality
 * @author pere
 */
public class UndoManager {

   private static int UNDO_BUFFER_CAPACITY = Constants.DEFAULT_BUFFER_SIZE;
   
   private int freePosition  = 0; //index for new edits
   private int fillCount     = 0; //
   private int startOfBuffer = 0; // index of the eldest element
   private int undoneEdits   = 0;

   private ArrayList<ArrayList> edits = new ArrayList(UNDO_BUFFER_CAPACITY);
   
   private GuiView view;
   private GraphDataLayerInterface model;
   private GraphPanel app;

   
   /**
    * Creates a new instance of UndoManager
    */
   public UndoManager(GuiView _view, GraphDataLayerInterface _model, GraphPanel _app) {
      view = _view;
      model = _model;
      app = _app;
//      app.setUndoActionEnabled(false);
//      app.setRedoActionEnabled(false);
      for (int i=0; i < UNDO_BUFFER_CAPACITY; i++){
         edits.add(null);
      }
   }
  
   
   public void doRedo() {

      if (undoneEdits > 0) {
         checkArcBeingDrawn();
         checkMode();

         // The currentEdit to redo
         Iterator<UndoableEdit> currentEdit = edits.get(freePosition).iterator();         
         while (currentEdit.hasNext()){
            currentEdit.next().redo();
         }
         freePosition = (freePosition + 1) % UNDO_BUFFER_CAPACITY;
         fillCount++; 
         undoneEdits--;
         if (undoneEdits==0){
            app.setRedoActionEnabled(false);
         }
         app.setUndoActionEnabled(true);
      }
   }
   
   
   public void doUndo() {

      if (fillCount > 0) {
         checkArcBeingDrawn();
         checkMode();

         if (--freePosition < 0){
            freePosition += UNDO_BUFFER_CAPACITY;
         }
         fillCount--;
         undoneEdits++;
         
         // The currentEdit to undo (reverse order)
         ArrayList<UndoableEdit> currentEdit = edits.get(freePosition);
         for (int i = currentEdit.size()-1; i >= 0; i--) {
            currentEdit.get(i).undo();
         }

         if (fillCount==0){
            app.setUndoActionEnabled(false);
         }
         app.setRedoActionEnabled(true);
      }
   }   
   
  
   public void clear() {
      freePosition = 0;
      fillCount   = 0;
      startOfBuffer  = 0;
      undoneEdits    = 0;
      app.setUndoActionEnabled(false);
      app.setRedoActionEnabled(false);
   }   
   
   
   public void newEdit(){
      ArrayList lastEdit = edits.get(currentIndex());
      if ((lastEdit != null) && (lastEdit.isEmpty())){
         return;
      }              
      
      undoneEdits = 0;
      app.setUndoActionEnabled(true);
      app.setRedoActionEnabled(false);
      view.setGraphChanged(true);
      
      ArrayList<UndoableEdit> compoundEdit = new ArrayList();
      edits.set(freePosition, compoundEdit);
      freePosition = (freePosition + 1) % UNDO_BUFFER_CAPACITY;
      if (fillCount < UNDO_BUFFER_CAPACITY){
         fillCount++;
      } else {
         startOfBuffer = (startOfBuffer + 1) % UNDO_BUFFER_CAPACITY;
      }       
   }
   
   
   public void addEdit(UndoableEdit undoableEdit){
      ArrayList<UndoableEdit> compoundEdit = edits.get(currentIndex());      
      compoundEdit.add(undoableEdit);
      //debug();
   }      
   
   
   public void addNewEdit(UndoableEdit undoableEdit) {
       newEdit(); // mark for a new "transtaction""
       addEdit(undoableEdit);
    }
   
   
   public void deleteSelection(GraphObject pnObject) {
      deleteObject(pnObject);
   }
   
   
   public void deleteSelection(ArrayList<GraphObject> selection) {
      for (GraphObject pnObject : selection) {
         deleteObject(pnObject);
      }
   }   

     
   public void translateSelection(ArrayList objects, int transX, int transY) {
      newEdit(); // new "transaction""
      Iterator<GraphObject> iterator = objects.iterator();
      while (iterator.hasNext()){
         addEdit(new TranslateGraphObjectEdit(
                 iterator.next(), transX, transY));
      }
   }

   
   private int currentIndex() {
      int lastAdd = freePosition - 1;
      if (lastAdd < 0){
         lastAdd += UNDO_BUFFER_CAPACITY;
      }
      return lastAdd;
   }

   
   // removes the arc currently being drawn if any
   private void checkArcBeingDrawn(){
      GraphArc arcBeingDrawn= view.createArc;
      if (arcBeingDrawn != null){
         if (arcBeingDrawn.getParent() != null) {
            arcBeingDrawn.getParent().remove(arcBeingDrawn);
         }
         view.createArc = null;
      }      
   }
   
   
   private void checkMode(){
      if ((app.getMode() == Constants.FAST_PLACE) ||
              (app.getMode() == Constants.FAST_TRANSITION)) {
         app.resetMode();
      }      
   }  
   
   
   private void deleteObject(GraphObject pnObject) {
      if (pnObject instanceof GraphArcPathPoint) {
         if (!((GraphArcPathPoint)pnObject).getArcPath().getArc().isSelected()){
            addEdit(new DeleteArcPathPointEdit(
                    ((GraphArcPathPoint)pnObject).getArcPath().getArc(), 
                    (GraphArcPathPoint)pnObject, ((GraphArcPathPoint)pnObject).getIndex()));
         } 
      } else {
         if (pnObject instanceof GraphAbstractNode) {
            //
            Iterator arcsTo = 
                    ((GraphAbstractNode)pnObject).getConnectToIterator();
            while (arcsTo.hasNext()) {
               GraphArc anArc = (GraphArc)arcsTo.next();
               if (!anArc.isDeleted()){  
                  addEdit(new DeleteGraphObjectEdit(anArc, view, model));
               }
            }            
            //
            Iterator arcsFrom = 
                    ((GraphAbstractNode)pnObject).getConnectFromIterator();
            while (arcsFrom.hasNext()) {
               GraphArc anArc = (GraphArc)arcsFrom.next();
               if (!anArc.isDeleted()){
                  addEdit(new DeleteGraphObjectEdit(anArc, view, model));
               }
            }

         }

         if (!pnObject.isDeleted()){
            addEdit(new DeleteGraphObjectEdit(pnObject, view, model));
            pnObject.delete();
         }
      }
   }
   
   
   private void debug(){
      int i = startOfBuffer;
      System.out.println("");
      for (int k = 0; k < fillCount; k++) {
         Iterator<UndoableEdit> currentEdit = edits.get(i).iterator();
         while (currentEdit.hasNext()) {
            System.out.println("["+ i + "]" + currentEdit.next().toString());
         }
         i = (i + 1 ) % UNDO_BUFFER_CAPACITY;
      }
   }
   
}
