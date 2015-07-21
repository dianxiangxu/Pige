package pige.dataLayer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;

import pige.gui.Constants;
import pige.gui.CreateGraphGui;
import pige.gui.Grid;
import pige.gui.ZoomController;

/**
 * @version 1.0
 * @author James D Bloom
 * 
 * @author Edwin Chung 16 Mar 2007: modified the constructor and several other
 *         functions so that DataLayer objects can be created outside the GUI
 */
public abstract class GraphAbstractNode extends GraphObject implements Cloneable {

	private static final long serialVersionUID = 1L;
	/** X-axis/Y-axis Position on screen */
	protected double positionX;
	protected double positionY;

	protected double componentWidth;
	protected double componentHeight;

	protected LinkedList<GraphArc> connectTo = new LinkedList<GraphArc>();
	protected LinkedList<GraphArc> connectFrom = new LinkedList<GraphArc>();
	protected static GraphArc someArc;

	private GraphAbstractNode lastCopy = null;
	private GraphAbstractNode original = null;
	private int copyNumber = 0;

	// The "real" x coordinate of this graph node.
	// i.e. the x position at 100% zoom.
	private double locationX;

	// The "real" y coordinate of this graph node.
	// i.e. the y position at 100% zoom.
	private double locationY;

	public GraphAbstractNode(double positionXInput, double positionYInput,
			String idInput, String nameInput, double nameOffsetXInput,
			double nameOffsetYInput) {
		setPositionX(positionXInput);
		setPositionY(positionYInput);

		id = idInput;
		setName(nameInput);
		name.setPosition((int) nameOffsetX, (int) nameOffsetY);

		nameOffsetX = nameOffsetXInput;
		nameOffsetY = nameOffsetYInput;
		
		if (CreateGraphGui.getGraphPanel() != null){
			this.addZoomController(CreateGraphGui.getView().getZoomController());
		}
	}

	public GraphAbstractNode(double positionXInput, double positionYInput, String idInput) {
		this(positionXInput, positionYInput, 
				idInput, idInput,Constants.DEFAULT_OFFSET_X, Constants.DEFAULT_OFFSET_Y);
	}

	public GraphAbstractNode(double positionXInput, double positionYInput) {
		this(positionXInput, positionYInput, CreateGraphGui.getModel().getUniqueGraphNodeID());
	}

	public void setPositionX(double positionXInput) {
		positionX = positionXInput;
		locationX = ZoomController.getUnzoomedValue(positionX, zoom);
	}

	public void setPositionY(double positionYInput) {
		positionY = positionYInput;
		locationY = ZoomController.getUnzoomedValue(positionY, zoom);
	}

	public double getPositionX() {
		return positionX;
	}

	public double getPositionY() {
		return positionY;
	}

	public Double getPositionXObject() {
		return new Double(locationX);
		// return new Double(positionX);
	}

	public Double getPositionYObject() {
		return new Double(locationY);
		// return new Double(positionY);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.translate(getComponentDrawOffset(), getComponentDrawOffset());
		g2.transform(ZoomController.getTransform(zoom));
	}

	public Point2D getIntersectOffset(Point2D start) {
		return new Point2D.Double();
	}

	/**
	 * Returns the distance between the outside of the component to the centre,
	 * in order to position the centre of the place where the mouse clicks on
	 * the screen
	 * 
	 * @return Top offset of Place
	 */
	public int centreOffsetTop() {
		return (int) (ZoomController.getZoomedValue(componentHeight / 2.0, zoom));
	}

	/**
	 * Returns the distance between the outside of the component to the centre,
	 * in order to position the centre of the place where the mouse clicks on
	 * the screen
	 * 
	 * @return Left offset of Place
	 */
	public int centreOffsetLeft() {
		return (int) (ZoomController.getZoomedValue(componentWidth / 2.0, zoom));
	}

	/** Calculates the BoundsOffsets used for setBounds() method */
	public void updateBounds() {
		double scaleFactor = ZoomController.getScaleFactor(zoom);
		positionX = locationX * scaleFactor;
		positionY = locationY * scaleFactor;
		bounds.setBounds((int) positionX, (int) positionY,
				(int) (componentWidth * scaleFactor),
				(int) (componentHeight * scaleFactor));
		bounds.grow(getComponentDrawOffset(), getComponentDrawOffset());
		setBounds(bounds);
	}

	/** Adds outwards arc  */
	public void addConnectTo(GraphArc newArc) {
		// System.out.println("DEBUG: added arc (to)!:" + newArc + " de " +
		// this);
		connectTo.add(newArc);
	}

	/** Adds inwards arc */
	public void addConnectFrom(GraphArc newArc) {
		// System.out.println("DEBUG: added arc (from)!:" + newArc + " de " +
		// this);
		connectFrom.add(newArc);
	}

	public void removeFromArc(GraphArc oldArc) {
		if (connectFrom.remove(oldArc)) {
			// System.out.println("DEBUG: removeFromArc_ok");
		} else {
			// System.out.println("DEBUG: removeFromArc_ko");
		}
	}

	public void removeToArc(GraphArc oldArc) {
		if (connectTo.remove(oldArc)) {
			// System.out.println("DEBUG: removeToArc_ok");
		} else {
			// System.out.println("DEBUG: removeToArc_ko");
		}
	}

	/** Updates location of any attached arcs */
	public void updateConnected() {
		Iterator<GraphArc> arcsFrom = connectFrom.iterator();
		while (arcsFrom.hasNext()) {
			someArc = ((GraphArc) arcsFrom.next());
			updateEndPoint(someArc);
			/*
			 *  Note: The below if statement is there due to erroneous behaviour.
			 *  When running tests updateEndPoint SOMETIMES caused someArc
			 *  to become null and SOMETIMES didn't. Since it was not consistent
			 *  it was very hard to find the root of the problem. This if statement
			 *  protects against it. 
			 */
			if (someArc != null) {
				someArc.updateArcPosition();
			}
		}

		Iterator<GraphArc> arcsTo = connectTo.iterator();
		while (arcsTo.hasNext()) {
			someArc = ((GraphArc) arcsTo.next());

			updateEndPoint(someArc);
			/*
			 *  Note: The below if statement is there due to erroneous behaviour.
			 *  When running tests updateEndPoint SOMETIMES caused someArc
			 *  to become null and SOMETIMES didn't. Since it was not consistent
			 *  it was very hard to find the root of the problem. This if statement
			 *  protects against it. 
			 */
			if (someArc != null) {
				someArc.updateArcPosition();
			}
		}
	}

	public Iterator<GraphArc> getArcsFrom() {
		return connectFrom.iterator();
	}

	public int getNoOfArcsFrom(){
		return connectFrom.size();
	}
	
	public Iterator<GraphArc> getArcsTo() {
		return connectTo.iterator();
	}

	public int getNoOfArcsTo(){
		return connectTo.size();
	}

	/** Translates the component by x,y */
	public void translate(int x, int y) {
		setPositionX(positionX + x);
		setPositionY(positionY + y);
		update();
	}

	/** Sets the center of the component to position x, y */
	public void setCentre(double x, double y) {
		setPositionX(x - (getWidth() / 2.0));
		setPositionY(y - (getHeight() / 2.0));
		update();
	}

	public void update() {
		updateBounds();
		updateLabelLocation();
		updateConnected();
	}

	public Point2D.Double getCentre() {
		return new Point2D.Double(positionX + getWidth() / 2.0, positionY
				+ getHeight() / 2.0);
	}

	private void updateLabelLocation() {
		name.setPosition(Grid.getModifiedX((int) (positionX + ZoomController
				.getZoomedValue(nameOffsetX, zoom))), Grid
				.getModifiedY((int) (positionY + ZoomController.getZoomedValue(
						nameOffsetY, zoom))));
	}

	public void delete() {
		if (getParent() != null) {
			getParent().remove(name);
		}
		super.delete();
	}

	/** Handles selection for Place/Transitions */
	public void select() {
		if (selectable && !selected) {
			selected = true;

			Iterator<GraphArc> arcsFrom = connectFrom.iterator();
			while (arcsFrom.hasNext()) {
				((GraphArc) arcsFrom.next()).select();
			}

			Iterator<GraphArc> arcsTo = connectTo.iterator();
			while (arcsTo.hasNext()) {
				((GraphArc) arcsTo.next()).select();
			}
			repaint();
		}
	}

	public void addedToGui() {
		deleted = false;
		markedAsDeleted = false;
		addLabelToContainer();
		update();
	}

	public boolean areNotSameType(GraphAbstractNode o) {
//		return (this.getClass() != o.getClass());
return true;
	}

	public Iterator<GraphArc> getConnectFromIterator() {
		return connectFrom.iterator();
	}

	public Iterator<GraphArc> getConnectToIterator() {
		return connectTo.iterator();
	}

	public abstract void updateEndPoint(GraphArc arc);

	public int getCopyNumber() {
		if (original != null) {
			original.copyNumber++;
			return original.copyNumber;
		} else {
			return 0;
		}
	}

	public void newCopy(GraphAbstractNode ptObject) {
		if (original != null) {
			original.lastCopy = ptObject;
		}
	}

	public GraphAbstractNode getLastCopy() {
		return lastCopy;
	}

	public void resetLastCopy() {
		lastCopy = null;
	}

	public void setOriginal(GraphAbstractNode ptObject) {
		original = ptObject;
	}

	public GraphAbstractNode getOriginal() {
		return original;
	}

	public abstract void showEditor();

	public int getLayerOffset() {
		return Constants.GRAPHNODE_LAYER_OFFSET;
	}

	public void zoomUpdate(int value) {
		zoom = value;
		update();
	}

	public GraphObject clone() {
		GraphObject graphObjectCopy = super.clone();
		graphObjectCopy.name = (NameLabel) name.clone();

		return graphObjectCopy;
	}
}
