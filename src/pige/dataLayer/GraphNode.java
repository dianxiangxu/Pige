package pige.dataLayer;

import java.awt.BasicStroke;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import javax.swing.BoxLayout;

import pige.gui.Constants;
import pige.gui.CreateGraphGui;
import pige.gui.Grid;
import pige.gui.ZoomController;
import pige.gui.widgets.EscapableDialog;
import pige.gui.widgets.GraphNodeEditorPanel;

/* @version 1.0
 * @author James D Bloom
 * 
 * @author Edwin Chung 16 Mar 2007
 * */
public class GraphNode extends GraphAbstractNode {
	private static final long serialVersionUID = 1L;
//	public final static String type = "GraphNode";

	public static final int DIAMETER = Constants.GRAPHNODE_HEIGHT;

	private static Ellipse2D.Double place = new Ellipse2D.Double(0, 0,
			DIAMETER, DIAMETER);
	private static Shape proximityPlace = (new BasicStroke(
			Constants.GRAPHNODE_PROXIMITY_RADIUS))
			.createStrokedShape(place);

	public enum SiblingRelation {AND, OR, PRIORITYAND};
	
	private SiblingRelation siblingRelation = SiblingRelation.PRIORITYAND;
	
	public GraphNode(
				double positionXInput,
				double positionYInput, 
				String idInput, 
				String nameInput,
				double nameOffsetXInput, 
				double nameOffsetYInput) 
	
		{
			super(
				positionXInput, 
				positionYInput, 
				idInput, 
				nameInput,
				nameOffsetXInput, 
				nameOffsetYInput);
				componentWidth = DIAMETER;
				componentHeight = DIAMETER;
				setCentre((int) positionX, (int) positionY);
		// updateBounds();
	}

	public GraphNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		componentWidth = DIAMETER;
		componentHeight = DIAMETER;
		setCentre((int) positionX, (int) positionY);
		// updateBounds();
	}

	public GraphNode paste(double x, double y, boolean fromAnotherView,
			GraphDataLayerInterface model) {
		GraphNode copy = GraphNodeFactory.createGraphNode(Grid.getModifiedX(x + this.getX()
				+ Constants.GRAPHNODE_HEIGHT / 2), Grid.getModifiedY(y
		+ this.getY() + Constants.GRAPHNODE_HEIGHT / 2));

		String newName = this.name.getName() + "_" + this.getCopyNumber();
		boolean properName = false;

		while (!properName) {
			if (model.checkGraphNodeIDAvailability(newName) == true) {
				copy.name.setName(newName);
				properName = true;
			} else {
				newName = newName + "'";
			}
		}
		this.newCopy(copy);
		copy.nameOffsetX = this.nameOffsetX;
		copy.nameOffsetY = this.nameOffsetY;
		copy.update();
		return copy;
	}

	public GraphNode copy() {
		GraphNode copy = GraphNodeFactory.createGraphNode(ZoomController.getUnzoomedValue(this.getX(), zoom), ZoomController.getUnzoomedValue(this.getY(), zoom));
		copy.name.setName(this.getName());
		copy.nameOffsetX = this.nameOffsetX;
		copy.nameOffsetY = this.nameOffsetY;
		copy.setOriginal(this);
		return copy;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (selected && !ignoreSelection) {
			g2.setColor(Constants.SELECTION_FILL_COLOUR);
		} else {
			g2.setColor(Constants.ELEMENT_FILL_COLOUR);
		}
		g2.fill(place);

		if (selected && !ignoreSelection) {
			g2.setPaint(Constants.SELECTION_LINE_COLOUR);
		} else {
			g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
		}
		g2.draw(place);

		g2.setStroke(new BasicStroke(1.0f));

	}


	private int getDiameter() {
		return (int) (ZoomController.getZoomedValue(DIAMETER, zoom));
	}

	public boolean contains(int x, int y) {
		double unZoomedX = ZoomController.getUnzoomedValue(x - getComponentDrawOffset(),
				zoom);
		double unZoomedY = ZoomController.getUnzoomedValue(y - getComponentDrawOffset(),
				zoom);

		someArc = CreateGraphGui.getView().createArc;
		if (someArc != null) { // Must be drawing a new Arc if non-NULL.
			if ((proximityPlace.contains((int) unZoomedX, (int) unZoomedY) || place
					.contains((int) unZoomedX, (int) unZoomedY))
					&& areNotSameType(someArc.getSource())
					) {
				// assume we are only snapping the target...
				if (someArc.getTarget() != this) {
					someArc.setTarget(this);
				}
				someArc.updateArcPosition();
				return true;
			} else {
				if (someArc.getTarget() == this) {
					someArc.setTarget(null);
					updateConnected();
				}
				return false;
			}
		} else {
			return place.contains((int) unZoomedX, (int) unZoomedY);
		}
	}

	public void updateEndPoint(GraphArc arc) {
		if (arc.getSource() == this) {
			// Make it calculate the angle from the centre of the place rather
			// than
			// the current start point
			arc.setSourceLocation(positionX + (getDiameter() * 0.5), positionY
					+ (getDiameter() * 0.5));
			double angle = arc.getArcPath().getStartAngle();
			arc.setSourceLocation(positionX + centreOffsetLeft()
					- (0.5 * getDiameter() * (Math.sin(angle))), positionY
					+ centreOffsetTop()
					+ (0.5 * getDiameter() * (Math.cos(angle))));
		} else {
			// Make it calculate the angle from the centre of the place rather
			// than the current target point
			arc.setTargetLocation(positionX + (getDiameter() * 0.5), positionY
					+ (getDiameter() * 0.5));
			double angle = arc.getArcPath().getEndAngle();
			arc.setTargetLocation(positionX + centreOffsetLeft()
					- (0.5 * getDiameter() * (Math.sin(angle))), positionY
					+ centreOffsetTop()
					+ (0.5 * getDiameter() * (Math.cos(angle))));
		}
	}

	public void addedToGui() {
		super.addedToGui();
		update();
	}

	public SiblingRelation getSiblingRelation(){
		return siblingRelation;
	}
	
	public void setSiblingRelation(SiblingRelation relation){
		this.siblingRelation = relation;
	}
	
	public void showEditor() {
		// Build interface
		EscapableDialog guiDialog = new EscapableDialog(CreateGraphGui.getParentFrame(),
				"PIGE", true);

		Container contentPane = guiDialog.getContentPane();

		// 1 Set layout
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

		// 2 Add Node editor
		contentPane.add(new GraphNodeEditorPanel(guiDialog.getRootPane(), this,
				CreateGraphGui.getModel(), CreateGraphGui.getView()));

		guiDialog.setResizable(false);

		// Make window fit contents' preferred size
		guiDialog.pack();

		// Move window to the middle of the screen
		guiDialog.setLocationRelativeTo(null);
		guiDialog.setVisible(true);
	}

	public String getSiblingRelationString(){
		return getSiblingRelationString(siblingRelation);
	}

	public String getSiblingRelationString(SiblingRelation relation){
		switch (relation){
			case AND: return "AND";
			case OR: return "OR";
			case PRIORITYAND: return "Priority AND";
		}		
		return "";
	}
	
	private String getAttributes(){
		if (CreateGraphGui.getModel()!=null
				&&	connectFrom.size()>1
			 && CreateGraphGui.graphType.hasNodeProperty()){
			String siblingRelationString = getSiblingRelationString(siblingRelation);
			if (!siblingRelationString.equals("AND"))
				return getName()+"\n["+siblingRelationString+"]";
		} 
		return getName();
	}

	public void update() {
		name.setText(getAttributes());;
		name.zoomUpdate(zoom);
		super.update();
		repaint();
	}

	public void delete() {
		super.delete();
	}

}
