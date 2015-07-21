package pige.gui.handler;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.SwingUtilities;

import pige.dataLayer.GraphAbstractNode;
import pige.dataLayer.NameLabel;


public class LabelHandler 
        extends javax.swing.event.MouseInputAdapter {

	private GraphAbstractNode obj;
	private NameLabel nameLabel;   
	protected Point dragInit = new Point(); 
   
   public LabelHandler(NameLabel _nameLabel, GraphAbstractNode _obj) {
      obj = (GraphAbstractNode)_obj;
      nameLabel = _nameLabel;
   }
   
   public void mouseClicked(MouseEvent e) {
      obj.dispatchEvent(e);
   }
   
   
   public void mousePressed(MouseEvent e) {
      dragInit = e.getPoint(); //
      dragInit = javax.swing.SwingUtilities.convertPoint(nameLabel, dragInit, obj);
   }
  

   public void mouseDragged(MouseEvent e){
      // 
      if (!SwingUtilities.isLeftMouseButton(e)){
         return;
      }
      
      Point p = javax.swing.SwingUtilities.convertPoint(nameLabel, e.getPoint(), obj);
      //obj.setNameOffsetX((e.getXOnScreen() - dragInit.x)); //causes exception in Windows!
      //obj.setNameOffsetY((e.getYOnScreen() - dragInit.y)); //causes exception in Windows!
      //dragInit = e.getLocationOnScreen(); //causes exception in Windows!
      obj.setNameOffsetX((p.x - dragInit.x));
      obj.setNameOffsetY((p.y - dragInit.y));
      dragInit = p;
      obj.update();
   }   
   
   public void mouseWheelMoved(MouseWheelEvent e) {
      obj.dispatchEvent(e);
   }
   
}
