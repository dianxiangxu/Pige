// Pige - Platform Independent Graph Editor
// Based on Pipe (Platform Independent Petri net Editor) 3.0: http://pipe2.sourceforge.net/
// Dianxiang Xu, August 2011

package pige.gui;

import java.io.File;
         
import javax.swing.JFrame;

import pige.dataLayer.GraphDataLayer;
import pige.dataLayer.GraphType;
import pigelocales.PigeLocales;


public class CreateGraphGui {
   
   private static JFrame parentFrame;

   private static GraphPanel graphPanel;
   
   public static String imgPath, userPath; 
   
   public static GraphType graphType;

   
   public static GraphPanel createGraphPanel(JFrame frame, File file, boolean isEditable, GraphType newGraphType){
	      PigeLocales.setResourceBundle();
	   	  imgPath = "pigeimages/";	      
	      userPath = file.getParent();; 
	      graphType = newGraphType;
	      if (graphType.allowsSelfArc())
	    	  Constants.GRAPHNODE_PROXIMITY_RADIUS = 0;
	      graphPanel = new GraphPanel(file, isEditable);
	      Grid.enableGrid();
	      parentFrame = frame;
	      return graphPanel;
   }
   
   public static GraphPanel getGraphPanel() { 
      return graphPanel;
   }
 
   public static JFrame getParentFrame() {  
	      return parentFrame;
	   }
	 
   public static GraphDataLayer getModel() {
      return graphPanel!=null? graphPanel.getModel(): null;
   }

   public static GuiView getView() {
	  return graphPanel!=null? graphPanel.getView(): null;
   }

   public static File getFile() {
      return graphPanel!=null? graphPanel.getFile(): null;
   }
   
}
