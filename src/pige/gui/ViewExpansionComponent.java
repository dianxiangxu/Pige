package pige.gui;

import pige.dataLayer.GraphDataLayerInterface;
import pige.dataLayer.GraphObject;

/**
 * @author unknown
 */
public class ViewExpansionComponent 
        extends GraphObject {

   private int originalX = 0;
   private int originalY = 0;
   
   
   public ViewExpansionComponent() {
      super();
   }
   
  
   public ViewExpansionComponent(int x, int y){
      this();
      originalX = x;
      originalY = y;
      setLocation(x,y);
   }
   

   public void zoomUpdate(int zoom) {
      double scaleFactor = ZoomController.getScaleFactor(zoom);
      setLocation((int)(originalX * scaleFactor),(int)(originalY * scaleFactor));
   }   

   
   public void addedToGui() {
      ;
   }

   
   public GraphObject copy() {
      return null;
   }

   
   public GraphObject paste(double despX, double despY, boolean inAnotherView, GraphDataLayerInterface model) {
      return null;
   }

   
   public int getLayerOffset() {
      return 0;
   }

   
   public void translate(int x, int y) {
      ;
   }

}
