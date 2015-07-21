/*
 * Translatable.java
 */

package pige.gui;

import pige.dataLayer.GraphDataLayerInterface;
import pige.dataLayer.GraphObject;


/**
 * This is the interface that a component must implement so that it can be 
 * copied and pasted.
 * @author Pere Bonet
 */
public interface CopyPasteable {

   public GraphObject copy();

   public GraphObject paste(double despX, double despY, 
           boolean notInTheSameView, GraphDataLayerInterface model);
   
   /**
    * isCopyPasteable();
    * @return true if this object can be copied and pasted
    */
   public boolean isCopyPasteable();
   
}
