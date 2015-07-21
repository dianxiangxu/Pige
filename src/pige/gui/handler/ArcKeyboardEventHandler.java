package pige.gui.handler;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import pige.dataLayer.GraphArc;
import pige.gui.Constants;
import pige.gui.CreateGraphGui;
import pige.gui.GuiView;



/**
 * @authors Michael Camacho and Tom Barnwell
 *
 */
public class ArcKeyboardEventHandler
        extends KeyAdapter {
   
   private GraphArc arcBeingDrawn;
   
   
   public ArcKeyboardEventHandler(GraphArc anArc) {
      arcBeingDrawn = anArc;
   }
   

   public void keyPressed(KeyEvent e) {
      switch (e.getKeyCode()) {
         case KeyEvent.VK_META:
         case KeyEvent.VK_WINDOWS:
            // I don't know if it's a java's bug or if I have a configuration 
            // problem with my linux box, but there is an issue with the 
            // Windows key under linux, so the space key is used as a provisional
            // solution
         case KeyEvent.VK_SPACE: //provisional
            ((GuiView)arcBeingDrawn.getParent()).setMetaDown(true);
            break;
            
         case KeyEvent.VK_ESCAPE:
         case KeyEvent.VK_DELETE:
            GuiView aView = ((GuiView)arcBeingDrawn.getParent());
            aView.createArc = null;
            arcBeingDrawn.delete();
            if ((CreateGraphGui.getGraphPanel().getMode() == Constants.FAST_PLACE) ||
                    (CreateGraphGui.getGraphPanel().getMode() == Constants.FAST_TRANSITION)) {
               CreateGraphGui.getGraphPanel().resetMode();
            }
            aView.repaint();
            break;
            
         default:
            break;
      }
   }
   
   
   public void keyReleased(KeyEvent e) {   
      switch (e.getKeyCode()) {
         case KeyEvent.VK_META:
         case KeyEvent.VK_WINDOWS:
         case KeyEvent.VK_SPACE: //provisional
            ((GuiView)arcBeingDrawn.getParent()).setMetaDown(false);
            break;
            
         default:
            break;
      }
      e.consume();
   }
   
}
