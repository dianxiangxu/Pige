package pige.gui.handler;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import pige.dataLayer.GraphNode;
import pige.gui.Constants;
import pige.gui.CreateGraphGui;
import pige.gui.ZoomController;
import pigelocales.PigeLocales;

/**
 * Class used to implement methods corresponding to mouse events on places.
 */
public class GraphNodeHandler extends GraphAbstractNodeHandler {

	public GraphNodeHandler(Container contentpane, GraphNode obj) {
		super(contentpane, obj);
	}

	/**
	 * Creates the popup menu that the user will see when they right click on a
	 * component
	 */
	public JPopupMenu getPopup(MouseEvent e) {
		int index = 0;
		JPopupMenu popup = super.getPopup(e);

		JMenuItem menuItem = new JMenuItem(PigeLocales.bundleString("Edit"));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((GraphNode) myObject).showEditor();
			}
		});
		popup.insert(menuItem, index++);

		return popup;
	}

	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)){
			if (e.getClickCount() == 2
					&& CreateGraphGui.getGraphPanel().isEditionAllowed()
					&& (CreateGraphGui.getGraphPanel().getMode() == Constants.GRAPHNODE || CreateGraphGui
							.getGraphPanel().getMode() == Constants.SELECT)) {
				((GraphNode) myObject).showEditor();
			} 
		} else if (SwingUtilities.isRightMouseButton(e)) {
			if (CreateGraphGui.getGraphPanel().isEditionAllowed() && enablePopup) {
				JPopupMenu m = getPopup(e);
				if (m != null) {
					int x = ZoomController.getZoomedValue(((GraphNode) myObject)
							.getNameOffsetXObject().intValue(), myObject
							.getZoom());
					int y = ZoomController.getZoomedValue(((GraphNode) myObject)
							.getNameOffsetYObject().intValue(), myObject
							.getZoom());
					m.show(myObject, x, y);
				}
			}
		}/*
		 * else if (SwingUtilities.isMiddleMouseButton(e)){ ; }
		 */
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
	}
}
