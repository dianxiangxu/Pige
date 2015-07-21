package pige.dataLayer;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.JLayeredPane;

import pige.gui.Constants;
import pige.gui.CreateGraphGui;
import pige.gui.GuiView;
import pige.gui.ZoomController;
import pige.gui.undo.AddArcPathPointEdit;
import pige.gui.undo.ArcPropertyEdit;
import pige.gui.undo.UndoableEdit;
import pige.gui.widgets.ArcPropertyEditorPanel;
import pige.gui.widgets.EscapableDialog;

/**
 * @version 1.0
 * @author James D Bloom
 * 
 * @author Edwin Chung 16 Mar 2007
 * 
 * @author Nick Dingle 18 Oct 2007
 * 
 * @author Dianxiang Xu, August 2011
 */

public class GraphArc extends GraphObject implements Cloneable {

	private static final long serialVersionUID = 1L;

	private final static Polygon head = new Polygon(new int[] { 0, 5, 0, -5 },
			new int[] { 0, -10, -7, -10 }, 4);

	protected String precondition = "";
	protected String postcondition ="";

	protected boolean showArcProperty = true;
	
	private static Point2D.Double point;

	/** references to the objects this arc connects */
	private GraphAbstractNode source = null;
	private GraphAbstractNode target = null;
	// private boolean deleted = false; // Used for cleanup purposes
	
	protected GraphArcPath myPath = new GraphArcPath(this);

	// bounds of arc need to be grown in order to avoid clipping problems
	protected int zoomGrow = 10;

	public GraphArc(double startPositionXInput, double startPositionYInput,
			double endPositionXInput, double endPositionYInput,
			GraphAbstractNode sourceInput,
			GraphAbstractNode targetInput, 
			String idInput, String nameInput, String precond, String postcond) {
		super();
		myPath.addPoint((float) startPositionXInput,
				(float) startPositionYInput, GraphArcPathPoint.STRAIGHT);
		myPath.addPoint((float) endPositionXInput, (float) endPositionYInput,
				GraphArcPathPoint.STRAIGHT);
		myPath.createPath();
		updateBounds();
		id = idInput;
		setName(nameInput);
		this.precondition = precond;
		this.postcondition = postcond;
		setSource(sourceInput);
		setTarget(targetInput);
		setLabelText();
		if (CreateGraphGui.getGraphPanel() != null){
			this.addZoomController(CreateGraphGui.getView().getZoomController());
		}
	}

	public GraphArc(GraphAbstractNode newSource) {
		super();
		id = CreateGraphGui.getModel().getUniqueArcId();
		source = newSource;
		myPath.addPoint();
		myPath.addPoint();
		myPath.createPath();
	}

	private GraphArc(GraphArc arc) {
		for (int i = 0; i <= arc.myPath.getEndIndex(); i++) {
			this.myPath.addPoint(arc.myPath.getPoint(i).getX(), arc.myPath
					.getPoint(i).getY(), arc.myPath.getPointType(i));
		}
		this.myPath.createPath();
		this.updateBounds();
		this.id = arc.id;
		this.setName(arc.getName());
		this.precondition = arc.precondition;
		this.postcondition = arc.postcondition;
		this.setSource(arc.getSource());
		this.setTarget(arc.getTarget());
	}

	public GraphArc paste(double despX, double despY, boolean toAnotherView,
			GraphDataLayerInterface model) {
		GraphAbstractNode source = this.getSource().getLastCopy();
		GraphAbstractNode target = this.getTarget().getLastCopy();

		if (source == null && target == null) {
			// don't paste an arc with neither source nor target
			return null;
		}

		if (source == null) {
			if (toAnotherView) {
				return null;
			} else {
				source = this.getSource();
			}
		}

		if (target == null) {
			if (toAnotherView) {
				return null;
			} else {
				target = this.getTarget();
			}
		}

		String uniqueArcId = CreateGraphGui.getModel()!=null? CreateGraphGui.getModel().getUniqueArcId(): "";
		GraphArc copy = new GraphArc(0, 0, 0, 0, source, target,
				uniqueArcId, getName(), precondition, postcondition);

		copy.myPath.delete();
		for (int i = 0; i <= this.myPath.getEndIndex(); i++) {
			copy.myPath.addPoint(this.myPath.getPoint(i).getX() + despX,
					this.myPath.getPoint(i).getY() + despY, this.myPath
							.getPointType(i));
			copy.myPath.selectPoint(i);
		}

		source.addConnectFrom(copy);
		target.addConnectTo(copy);

		return copy;
	}

	public GraphArc copy() {
		return new GraphArc(this);
	}
	
	public void setSource(GraphAbstractNode sourceInput) {
		source = sourceInput;
	}

	public void setTarget(GraphAbstractNode targetInput) {
		target = targetInput;
	}

	private void setLabelText(){
		if (CreateGraphGui.graphType.hasArcProperty()){
			if (showArcProperty && (!precondition.equals("") || !postcondition.equals(""))){
				String newLine = !precondition.equals("") && !postcondition.equals("")? "\n": ""; 
				String comma = !postcondition.equals("")? ", ": "";
				name.setText(getName()+"\n["+precondition+comma+newLine+postcondition+"]");
			}
			else 
				name.setText(getName());
		} else
			name.setText("");
	}

	public void setArcLabelPosition() {
		int x = (int) (myPath.midPoint.x);
		int y = (int) (myPath.midPoint.y) - 10;
		if( !name.getText().trim().equals("")){
				name.setPosition(x + name.getWidth() / 2 - 4, y);
		}
	}

	public GraphAbstractNode getSource() {
		return source;
	}

	public GraphAbstractNode getTarget() {
		return target;
	}

	public double getStartPositionX() {
		return myPath.getPoint(0).getX();
	}

	public double getStartPositionY() {
		return myPath.getPoint(0).getY();
	}

	public void updateArcPosition() {
		if (source != null) {
			source.updateEndPoint(this);
		}
		if (target != null) {
			target.updateEndPoint(this);
		}
		myPath.createPath();
	}

	public void setEndPoint(double x, double y, boolean type) {
		myPath.setPointLocation(myPath.getEndIndex(), x, y);
		myPath.setPointType(myPath.getEndIndex(), type);
		updateArcPosition();
	}

	public void setTargetLocation(double x, double y) {
		myPath.setPointLocation(myPath.getEndIndex(), x, y);
		myPath.createPath();
		updateBounds();
		repaint();
	}

	public void setSourceLocation(double x, double y) {
		myPath.setPointLocation(0, x, y);
		myPath.createPath();
		updateBounds();
		repaint();
	}

	/** Updates the bounding box of the arc component based on the arcs bounds */
	public void updateBounds() {
		bounds = myPath.getBounds();
		bounds.grow(getComponentDrawOffset() + zoomGrow, getComponentDrawOffset()
				+ zoomGrow);
		setBounds(bounds);
	}

	public GraphArcPath getArcPath() {
		return myPath;
	}

	public boolean contains(int x, int y) {
		point = new Point2D.Double(x + myPath.getBounds().getX()
				- getComponentDrawOffset() - zoomGrow, y
				+ myPath.getBounds().getY() - getComponentDrawOffset() - zoomGrow);
		if (myPath.proximityContains(point) || selected) {
			// show also if Arc itself selected
			myPath.showPoints();
		} else {
			myPath.hidePoints();
		}
		return myPath.contains(point);
	}

	public void addedToGui() {
		// called by GuiView / State viewer when adding component.
		deleted = false;
		markedAsDeleted = false;

		if (getParent() instanceof GuiView) {
			myPath.addPointsToGui((GuiView) getParent());
		} else {
			myPath.addPointsToGui((JLayeredPane) getParent());
		}
		updateArcPosition();
		addLabelToContainer();
		update();
	}

	public void delete() {
		if (!deleted) {
			getParent().remove(name);
			myPath.forceHidePoints();
			super.delete();
			deleted = true;
		}
	}

	public UndoableEdit split(Point2D.Float mouseposition) {
		GraphArcPathPoint newPoint = myPath.splitSegment(mouseposition);
		return new AddArcPathPointEdit(this, newPoint);
	}

	public UndoableEdit setArcProperties(String newName, String newPrecond, String newPostcond){
		String oldName = this.getName();
		this.setName(newName);
		String oldPrecond = this.precondition;
		this.precondition = newPrecond;
		String oldPostcond = this.postcondition;
		this.postcondition = newPostcond;

		update();
		return new ArcPropertyEdit(this, oldName, newName, oldPrecond, newPrecond, oldPostcond, newPostcond);               
	}

	public String getPrecondition(){
		return precondition;
	}
	
	public void setPrecondition(String precond){
		this.precondition = precond;
	}

	public String getPostcondition(){
		return postcondition;
	}

	public void setPostcondition(String postcond){
		this.postcondition = postcond;
	}

	public boolean showArcProperty(){
		return showArcProperty;
	}
	
	public void toggleShowArcProperty(){
		showArcProperty = !showArcProperty;
		update();
	}
	
	public void removeFromView() {
		if (getParent() != null) {
			getParent().remove(name);
		}
		myPath.forceHidePoints();
		removeFromContainer();
	}

	public void addToView(GuiView view) {
		if (getParent() != null) {
				getParent().add(name);
		}
		myPath.showPoints();
	    view.add(this);
	}
	
	public boolean getsSelected(Rectangle selectionRectangle) {
		if (selectable) {
			GraphArcPath arcPath = getArcPath();
			if (arcPath.proximityIntersects(selectionRectangle)) {
				arcPath.showPoints();
			} else {
				arcPath.hidePoints();
			}
			if (arcPath.intersects(selectionRectangle)) {
				select();
				return true;
			}
		}
		return false;
	}

	public int getLayerOffset() {
		return Constants.ARC_LAYER_OFFSET;
	}

	public void translate(int x, int y) {
		// We don't translate an arc, we translate each selected arc point
	}

	public void zoomUpdate(int percent) {
		zoom = percent;
		this.updateArcPosition();
		name.zoomUpdate(percent);
		name.updateSize();
	}

	public void setZoom(int percent) {
		zoom = percent;
	}

	public void undelete(GraphDataLayerInterface model, GuiView view) {
		if (this.isDeleted()) {
			model.addGraphObject(this);
			view.add(this);
			getSource().addConnectFrom(this);
			getTarget().addConnectTo(this);
		}
	}

	public GraphObject clone() {
		return (GraphArc) super.clone();
	}

	public void update() {
		setLabelText();
		name.zoomUpdate(zoom);
		repaint();
	}
	
	public void showEditor() {
		EscapableDialog guiDialog = new EscapableDialog(CreateGraphGui.getParentFrame(),
				"PIGE", true);

		ArcPropertyEditorPanel arcPropertyEditor = new ArcPropertyEditorPanel(
				guiDialog.getRootPane(), this, CreateGraphGui.getModel(), CreateGraphGui
						.getView());

		guiDialog.add(arcPropertyEditor);
		guiDialog.getRootPane().setDefaultButton(null);
		guiDialog.setResizable(false);
		// Make window fit contents' preferred size
		guiDialog.pack();
		// Move window to the middle of the screen
		guiDialog.setLocationRelativeTo(null);
		guiDialog.setVisible(true);
		guiDialog.dispose();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.translate(getComponentDrawOffset() + zoomGrow
				- myPath.getBounds().getX(), getComponentDrawOffset() + zoomGrow
				- myPath.getBounds().getY());

		AffineTransform reset = g2.getTransform();

		if (selected && !ignoreSelection) {
			g2.setPaint(Constants.SELECTION_LINE_COLOUR);
		} else {
			g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
		}

		g2.setStroke(new BasicStroke(0.01f * zoom));
		g2.draw(myPath);

		g2.translate(myPath.getPoint(myPath.getEndIndex()).getX(), myPath
				.getPoint(myPath.getEndIndex()).getY());

		g2.rotate(myPath.getEndAngle() + Math.PI);
		g2.setColor(java.awt.Color.WHITE);

		g2.transform(ZoomController.getTransform(zoom));
		g2.setPaint(Constants.ELEMENT_LINE_COLOUR);

		if (selected && !ignoreSelection) {
			g2.setPaint(Constants.SELECTION_LINE_COLOUR);
		} else {
			g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
		}

		g2.setStroke(new BasicStroke(0.8f));
		g2.fillPolygon(head);

		g2.transform(reset);
	}
}
