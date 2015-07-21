package pige.dataLayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Observable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pige.gui.Constants;
import pige.gui.CreateGraphGui;
import pige.gui.Grid;

/**
 * @version 1.0
 * @author James D Bloom
 * 
 * @author David Patterson Jan 2, 2006
 *  
 * @author Edwin Chung,(6th Feb 2007)
 * 
 * @author Ben Kirby Feb 10, 2007: 
 *  
 * @author Ben Kirby Feb 10, 2007
 * 
 * @author Will Master Feb 13 2007
 * 
 * @author Edwin Chung 15th Mar 2007
 *  
 * @author Dave Patterson 24 April 2007
 * 
 * @author Dave Patterson 10 May 2007
 * 
 * @author Barry Kearns August 2007
 * 
 **/
public class GraphDataLayer extends Observable implements Cloneable, GraphDataLayerInterface {

	/** XML File Name */
	public String xmlName = null;

	private ArrayList<GraphNode> nodesArray = null;
	private ArrayList<GraphArc> arcsArray = null;

	private ArrayList<GraphAnnotationNote> labelsArray = null;
	
	@SuppressWarnings("unchecked")
	private ArrayList changeArrayList = null;


	/** X-Axis Scale Value */
	private final int DISPLAY_SCALE_FACTORX = 7;

	/** Y-Axis Scale Value */
	private final int DISPLAY_SCALE_FACTORY = 7; 

	/** X-Axis Shift Value */
	private final int DISPLAY_SHIFT_FACTORX = 270; 
	
	/** Y-Axis Shift Value */
	private final int DISPLAY_SHIFT_FACTORY = 120; 

	private Hashtable arcsMap = null;

	public GraphDataLayer(String xmlFileName) {
		initializeMatrices();
		XMLTransformer transform = new XMLTransformer();
		File temp = new File(xmlFileName);
		xmlName = temp.getName();
		createFromXML(transform.transformXML(xmlFileName));
	}

	public GraphDataLayer(File xmlFile) {
		this(xmlFile.getAbsolutePath());
	}

	/**
	 * Create empty graph
	 */
	public GraphDataLayer() {
		initializeMatrices();
	}

	public GraphDataLayer clone() {
		GraphDataLayer newClone = null;
		try {
			newClone = (GraphDataLayer) super.clone();
			newClone.nodesArray = deepCopy(nodesArray);
			newClone.arcsArray = deepCopy(arcsArray);
			newClone.labelsArray = deepCopy(labelsArray);
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
		return newClone;
	}

	private static ArrayList deepCopy(ArrayList original) {
		ArrayList result = (ArrayList) original.clone();
		ListIterator listIter = result.listIterator();

		while (listIter.hasNext()) {
			GraphObject pnObj = (GraphObject) listIter.next();
			listIter.set(pnObj.clone());
		}
		return result;
	}

	/**
	 * Initialize Arrays
	 */
	private void initializeMatrices() {
		nodesArray = new ArrayList();
		arcsArray = new ArrayList();
		labelsArray = new ArrayList();
		// may as well do the hashtable here as well
		arcsMap = new Hashtable();
	}

	public String getUniqueGraphNodeID(){
		String idPrefix = CreateGraphGui.graphType.getNodeIdPrefix();
		int max=0;
		for (GraphNode node: nodesArray){
			int nodeId = 0;
			try {
				nodeId = Integer.parseInt(node.getId().substring(idPrefix.length()));
			}
			catch (Exception e){
			}
			if (nodeId>max)
				max=nodeId;
		}
		return idPrefix+(max+1);
	}
	
	private void addGraphNode(GraphNode nodeInput) {
		if (nodeInput != null) {
			if (nodeInput.getId() != null && nodeInput.getId().length() > 0) {
				boolean unique = true;
				for (int i = 0; i < nodesArray.size(); i++) {
					if (nodeInput.getId().equals(
							((GraphNode) nodesArray.get(i)).getId())) {
						nodeInput.setId(getUniqueGraphNodeID());
						unique = false;
						break;
					}
				}
				if (!unique)
					nodeInput.setId(getUniqueGraphNodeID());
			} else {
					nodeInput.setId(getUniqueGraphNodeID());
			}
			nodesArray.add(nodeInput);
			setChanged();
			notifyObservers(nodeInput);
		}
	}

	/**
	 * Add labelInput to the back of the AnnotationNote ArrayList All observers
	 * are notified of this change (Model-View Architecture)
	 * 
	 * @param labelInput
	 *            AnnotationNote Object to add
	 */
	private void addAnnotation(GraphAnnotationNote labelInput) {
		labelsArray.add(labelInput);
		setChanged();
		notifyObservers(labelInput);
	}

	public String getUniqueArcId(){
		String idPrefix = CreateGraphGui.graphType.getArcIdPrefix();
		int max=0;
		for (GraphArc arc: arcsArray){
			int arcId = 0;
			try {
				arcId = Integer.parseInt(arc.getId().substring(idPrefix.length()));
			}
			catch (Exception e){
			}
			if (arcId>max)
				max=arcId;
		}
		return idPrefix+(max+1);
	}

	
	public void addArc(GraphArc arcInput) {
		if (arcInput != null) {
			arcsArray.add(arcInput);
			addArcToArcsMap(arcInput);
			setChanged();
			// notifyObservers(arcInput.getBounds());
			notifyObservers(arcInput);
		}
	}


	/**
	 * Update the arcsMap hashtable to reflect the new arc
	 * 
	 * @param arcInput
	 *            New Arc
	 * */
	private void addArcToArcsMap(GraphArc arcInput) {
		// now we want to add the arc to the list of arcs for it's source and
		// target
		GraphAbstractNode source = arcInput.getSource();
		GraphAbstractNode target = arcInput.getTarget();
		ArrayList newList = null;

		if (source != null) {
			// source.setMovable(false);
			if (arcsMap.get(source) != null) {
				((ArrayList) arcsMap.get(source)).add(arcInput);
			} else {
				newList = new ArrayList();
				newList.add(arcInput);

				arcsMap.put(source, newList);
			}
		}

		if (target != null) {
			if (arcsMap.get(target) != null) {
				((ArrayList) arcsMap.get(target)).add(arcInput);
			} else {
				newList = new ArrayList();
				newList.add(arcInput);
				arcsMap.put(target, newList);
			}
		}
	}

	
	public void addGraphObject(GraphObject graphObject) {
		if (setGraphObjectArrayList(graphObject)) {
			if (graphObject instanceof GraphArc) {
				addArcToArcsMap((GraphArc) graphObject);
				addArc((GraphArc) graphObject);
			} else if (graphObject instanceof GraphNode) {
				addGraphNode((GraphNode) graphObject);
			} else if (graphObject instanceof GraphAnnotationNote) {
				labelsArray.add((GraphAnnotationNote) graphObject);
			} else { // arrows, other labels.
				changeArrayList.add(graphObject);
				setChanged();
				notifyObservers(graphObject);
			}
		}
		// we reset to null so that the wrong ArrayList can't get added to
		changeArrayList = null;
	}

	public void removeGraphObject(GraphObject graphObject) {
		boolean didSomething = false;
		ArrayList attachedArcs = null;

		try {
			if (setGraphObjectArrayList(graphObject)) {
				didSomething = changeArrayList.remove(graphObject);
				// we want to remove all attached arcs also
				if (graphObject instanceof GraphAbstractNode) {

					if ((ArrayList) arcsMap.get(graphObject) != null) {

						// get the list of attached arcs for the object we are
						// removing
						attachedArcs = ((ArrayList) arcsMap.get(graphObject));

						// iterate over all the attached arcs, removing them all
						// Pere: in inverse order!
						// for (int i=0; i < attachedArcs.size(); i++){
						for (int i = attachedArcs.size() - 1; i >= 0; i--) {
							if (i<attachedArcs.size())
							((GraphArc) attachedArcs.get(i)).delete();
						}
						arcsMap.remove(graphObject);
					}

				} else if (graphObject instanceof GraphArc) {

					// get source and target of the arc
					GraphAbstractNode attached = ((GraphArc) graphObject)
							.getSource();

					if (attached != null) {
						ArrayList a = (ArrayList) arcsMap.get(attached);
						if (a != null) {
							a.remove(graphObject);
						}

						attached.removeFromArc((GraphArc) graphObject);
						// attached.updateConnected(); //causing null pointer
						// exceptions (?)
					}

					attached = ((GraphArc) graphObject).getTarget();
					if (attached != null) {
						if (arcsMap.get(attached) != null) { // causing null
							// pointer
							// exceptions
							// (!)
							((ArrayList) arcsMap.get(attached))
									.remove(graphObject);
						}

						attached.removeToArc((GraphArc) graphObject);
						// attached.updateConnected(); //causing null pointer
						// exceptions (?)
					}
				} 

				if (didSomething) {
					setChanged();
					// notifyObservers(pnObject.getBounds());
					notifyObservers(graphObject);
				}
			}
		} catch (NullPointerException npe) {
			System.out.println("NullPointerException [debug]\n"
					+ npe.getMessage());
			throw npe;
		}
		// we reset to null so that the wrong ArrayList can't get added to
		changeArrayList = null;
	}

	private boolean setGraphObjectArrayList(GraphObject graphObject) {

		if (graphObject instanceof GraphNode) {
			changeArrayList = nodesArray;
			return true;
		} else if (graphObject instanceof GraphArc) {
			changeArrayList = arcsArray;
			return true;
		} else if (graphObject instanceof GraphAnnotationNote) {
			changeArrayList = labelsArray;
			return true;
		} 
		return false;
	}

	public Iterator getGraphObjects() {
		ArrayList all = new ArrayList(nodesArray);
		all.addAll(arcsArray);
		all.addAll(labelsArray);
		// tokensArray removed

		return all.iterator();
	}

	public boolean hasGraphObjects() {
		return (nodesArray.size()) > 0;
	}

	private GraphAnnotationNote createAnnotation(Element inputLabelElement) {
		int positionXInput = 0;
		int positionYInput = 0;
		int widthInput = 0;
		int heightInput = 0;
		String text = null;
		boolean borderInput = true;

		String positionXTempStorage = inputLabelElement
				.getAttribute("xPosition");
		String positionYTempStorage = inputLabelElement
				.getAttribute("yPosition");
		String widthTemp = inputLabelElement.getAttribute("w");
		String heightTemp = inputLabelElement.getAttribute("h");
		String textTempStorage = inputLabelElement.getAttribute("txt");
		String borderTemp = inputLabelElement.getAttribute("border");

		if (positionXTempStorage.length() > 0) {
			positionXInput = Integer.valueOf(positionXTempStorage).intValue()
					* (false ? DISPLAY_SCALE_FACTORX : 1)
					+ (false ? DISPLAY_SHIFT_FACTORX : 1);
		}

		if (positionYTempStorage.length() > 0) {
			positionYInput = Integer.valueOf(positionYTempStorage).intValue()
					* (false ? DISPLAY_SCALE_FACTORX : 1)
					+ (false ? DISPLAY_SHIFT_FACTORX : 1);
		}

		if (widthTemp.length() > 0) {
			widthInput = Integer.valueOf(widthTemp).intValue()
					* (false ? DISPLAY_SCALE_FACTORY : 1)
					+ (false ? DISPLAY_SHIFT_FACTORY : 1);
		}

		if (heightTemp.length() > 0) {
			heightInput = Integer.valueOf(heightTemp).intValue()
					* (false ? DISPLAY_SCALE_FACTORY : 1)
					+ (false ? DISPLAY_SHIFT_FACTORY : 1);
		}

		if (borderTemp.length() > 0) {
			borderInput = Boolean.valueOf(borderTemp).booleanValue();
		} else {
			borderInput = true;
		}

		if (textTempStorage.length() > 0) {
			text = textTempStorage;
		} else {
			text = "";
		}

		return new GraphAnnotationNote(text, positionXInput, positionYInput,
				widthInput, heightInput, borderInput);
	}


	private GraphNode createGraphNode(Element element) {
		double positionXInput = 0;
		double positionYInput = 0;
		String idInput = null;
		String nameInput = null;
		double nameOffsetYInput = 0;
		double nameOffsetXInput = 0;

		String positionXTempStorage = element.getAttribute("positionX");
		String positionYTempStorage = element.getAttribute("positionY");
		String idTempStorage = element.getAttribute("id");
		String nameTempStorage = element.getAttribute("name");
		String nameOffsetXTempStorage = element.getAttribute("nameOffsetX");
		String nameOffsetYTempStorage = element.getAttribute("nameOffsetY");

		if (positionXTempStorage.length() > 0) {
			positionXInput = Double.valueOf(positionXTempStorage).doubleValue()
					* (false ? Constants.DISPLAY_SCALE_FACTORX : 1)
					+ (false ? Constants.DISPLAY_SHIFT_FACTORX : 1);
		}
		if (positionYTempStorage.length() > 0) {
			positionYInput = Double.valueOf(positionYTempStorage).doubleValue()
					* (false ? Constants.DISPLAY_SCALE_FACTORY : 1)
					+ (false ? Constants.DISPLAY_SHIFT_FACTORY : 1);
		}
		positionXInput = Grid.getModifiedX(positionXInput);
		positionYInput = Grid.getModifiedY(positionYInput);

		if (idTempStorage.length() > 0) {
			idInput = idTempStorage;
		} else if (nameTempStorage.length() > 0) {
			idInput = nameTempStorage;
		}

		if (nameTempStorage.length() > 0) {
			nameInput = nameTempStorage;
		} else if (idTempStorage.length() > 0) {
			nameInput = idTempStorage;
		}

		if (nameOffsetYTempStorage.length() > 0) {
			nameOffsetXInput = Double.valueOf(nameOffsetXTempStorage)
					.doubleValue();
		}
		if (nameOffsetXTempStorage.length() > 0) {
			nameOffsetYInput = Double.valueOf(nameOffsetYTempStorage)
					.doubleValue();
		}

		GraphNode graphNode = GraphNodeFactory.createGraphNode(positionXInput, positionYInput, idInput, nameInput, nameOffsetXInput,
				nameOffsetYInput);

		return graphNode;
	}

	/**
	 * Creates a Arc object from a Arc DOM Element
	 * 
	 * @param inputArcElement
	 *            Input Arc DOM Element
	 * @return Arc Object
	 */
	private GraphArc createArc(Element inputArcElement) {

		double inscriptionOffsetXInput = 0;
		double inscriptionOffsetYInput = 0;
		double startX = 0;
		double startY = 0;
		double endX = 0;
		double endY = 0;

		String idInput = inputArcElement.getAttribute("id");
		String eventInput = inputArcElement.getAttribute("event");
		String precondInput = inputArcElement.getAttribute("precondition");
		String postcondInput = inputArcElement.getAttribute("postcondition");
		String sourceInput = inputArcElement.getAttribute("source");
		String targetInput = inputArcElement.getAttribute("target");

		if (sourceInput.trim().length() == 0 ||
				targetInput.trim().length() == 0)
			return null;  // invalid arc description

		if (sourceInput.length() > 0) {
			if (getAbstractNodeObject(sourceInput) != null) {
				startX = getAbstractNodeObject(sourceInput).getPositionX();
				startX += getAbstractNodeObject(sourceInput)
						.centreOffsetLeft();
				startY = getAbstractNodeObject(sourceInput).getPositionY();
				startY += getAbstractNodeObject(sourceInput)
						.centreOffsetTop();
			}
		}
		if (targetInput.length() > 0) {
			if (getAbstractNodeObject(targetInput) != null) {
				endX = getAbstractNodeObject(targetInput).getPositionX();
				endY = getAbstractNodeObject(targetInput).getPositionY();
			}
		}

		GraphAbstractNode sourceIn = getAbstractNodeObject(sourceInput);
		GraphAbstractNode targetIn = getAbstractNodeObject(targetInput);

		// add the insets and offset
		int aStartx = sourceIn.getX() + sourceIn.centreOffsetLeft();
		int aStarty = sourceIn.getY() + sourceIn.centreOffsetTop();

		int aEndx = targetIn.getX() + targetIn.centreOffsetLeft();
		int aEndy = targetIn.getY() + targetIn.centreOffsetTop();

		double _startx = aStartx;
		double _starty = aStarty;
		double _endx = aEndx;
		double _endy = aEndy;
		// TODO

		GraphArc tempArc;

		String type = "normal"; // default value
		NodeList nl = inputArcElement.getElementsByTagName("type");
		if (nl.getLength() > 0) {
			type = ((Element) (nl.item(0))).getAttribute("type");
		}

		tempArc = new GraphArc(_startx, _starty, _endx, _endy,
				sourceIn, targetIn, idInput, eventInput, precondInput, postcondInput);

		getAbstractNodeObject(sourceInput).addConnectFrom(tempArc);
		getAbstractNodeObject(targetInput).addConnectTo(tempArc);

		// **********************************************************************************
		// The following section attempts to load and display arcpath
		// details****************

		// NodeList nodelist = inputArcElement.getChildNodes();
		NodeList nodelist = inputArcElement.getElementsByTagName("arcpath");
		if (nodelist.getLength() > 0) {
			tempArc.getArcPath().purgePathPoints();
			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node = nodelist.item(i);
				if (node instanceof Element) {
					Element element = (Element) node;
					if ("arcpath".equals(element.getNodeName())) {
						String arcTempX = element.getAttribute("x");
						String arcTempY = element.getAttribute("y");
						String arcTempType = element
								.getAttribute("arcPointType");
						float arcPointX = Float.valueOf(arcTempX).floatValue();
						float arcPointY = Float.valueOf(arcTempY).floatValue();
						arcPointX += Constants.ARC_CONTROL_POINT_CONSTANT + 1;
						arcPointY += Constants.ARC_CONTROL_POINT_CONSTANT + 1;
						boolean arcPointType = Boolean.valueOf(arcTempType)
								.booleanValue();
						tempArc.getArcPath().addPoint(arcPointX, arcPointY,
								arcPointType);
					}
				}
			}
		}

		// Arc path creation ends
		// here***************************************************************
		// ******************************************************************************************
		return tempArc;
	}


	private void emptyXML() {
		xmlName = null;
		nodesArray = null;
		arcsArray = null;
		labelsArray = null;
		changeArrayList = null;
		arcsMap = null;
		initializeMatrices();
	}

	public int getListPosition(GraphObject graphObject) {

		if (setGraphObjectArrayList(graphObject)) {
			return changeArrayList.indexOf(graphObject);
		} else {
			return -1;
		}
	}

	public GraphNode[] getGraphNodes() {
		GraphNode[] returnArray = new GraphNode[nodesArray.size()];

		for (int i = 0; i < nodesArray.size(); i++) {
			returnArray[i] = (GraphNode) nodesArray.get(i);
		}
		return returnArray;
	}
	
	public ArrayList<GraphNode> getGraphNodesArrayList() {
		return nodesArray;
	}
	
	public int getGraphNodesCount() {
		if (nodesArray == null) {
			return 0;
		} else {
			return nodesArray.size();
		}
	}

	public GraphAnnotationNote[] getLabels() {
		GraphAnnotationNote[] returnArray = new GraphAnnotationNote[labelsArray.size()];

		for (int i = 0; i < labelsArray.size(); i++) {
			returnArray[i] = (GraphAnnotationNote) labelsArray.get(i);
		}
		return returnArray;
	}

	public GraphArc[] getArcs() {
		GraphArc[] returnArray = new GraphArc[arcsArray.size()];

		for (int i = 0; i < arcsArray.size(); i++) {
			returnArray[i] = (GraphArc) arcsArray.get(i);
		}
		return returnArray;
	}

	public ArrayList<GraphArc> getArcsArrayList() {
		return arcsArray;
	}

	public GraphNode getGraphNodeById(String nodeId) {
		GraphNode returnNode = null;

		if (nodesArray != null) {
			if (nodeId != null) {
				for (int i = 0; i < nodesArray.size(); i++) {
					if (nodeId.equalsIgnoreCase(((GraphNode) nodesArray.get(i))
							.getId())) {
						returnNode = (GraphNode) nodesArray.get(i);
					}
				}
			}
		}
		return returnNode;
	}

	public GraphNode getGraphNodeByName(String nodeName) {
		GraphNode returnNode = null;

		if (nodesArray != null) {
			if (nodeName != null) {
				for (int i = 0; i < nodesArray.size(); i++) {
					if (nodeName.equalsIgnoreCase(((GraphNode) nodesArray.get(i))
							.getName())) {
						returnNode = (GraphNode) nodesArray.get(i);
					}
				}
			}
		}
		return returnNode;
	}

	public GraphNode getGraphNode(int nodeNo) {
		GraphNode returnNode = null;

		if (nodesArray != null) {
			if (nodeNo < nodesArray.size()) {
				returnNode = (GraphNode) nodesArray.get(nodeNo);
			}
		}
		return returnNode;
	}

	public GraphAbstractNode getAbstractNodeObject(String ptoId) {
		if (ptoId != null) {
			if (getGraphNodeById(ptoId) != null) {
				return getGraphNodeById(ptoId);
			} 
		}
		return null;
	}

	public void createFromXML(Document PNMLDoc) {
		emptyXML();
		Element element = null;
		Node node = null;
		NodeList nodeList = null;

		try {
			nodeList = PNMLDoc.getDocumentElement().getChildNodes();
			if (CreateGraphGui.getGraphPanel() != null) {
				// Notifies used to indicate new instances.
				CreateGraphGui.getGraphPanel().setMode(Constants.CREATING);
			}
//			System.out.println("Loading...");

			for (int i = 0; i < nodeList.getLength(); i++) {
				node = nodeList.item(i);
//System.out.println("Node "+i+": "+node);

				if (node instanceof Element) {
					element = (Element) node;
					if ("labels".equals(element.getNodeName())) {
						addAnnotation(createAnnotation(element));
					} else if ("node".equals(element.getNodeName())) {
						addGraphNode(createGraphNode(element));
					} else if ("arc".equals(element.getNodeName())) {
						GraphArc newArc = createArc(element);
						if (newArc!=null)
							addArc((GraphArc) newArc);
					} else {
						System.out.println("!" + element.getNodeName());
					}
				}
			}

			if (CreateGraphGui.getGraphPanel() != null) {
				CreateGraphGui.getGraphPanel().restoreMode();
			}
//			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	
	public String getURI() {
		return xmlName;
	}

	public void print() {
		System.out.println("No of Nodes = " + nodesArray.size() + "\"");
		System.out.println("No of Arcs = " + arcsArray.size() + "\"");
		System.out.println("No of Labels = " + labelsArray.size()
				+ "\" (Model View Controller Design Pattern)");
	}

	public boolean checkGraphNodeIDAvailability(String newName) {
		for (int i = 0; i < nodesArray.size(); i++) {
			if (((GraphNode) (nodesArray.get(i))).getName().equals(newName)) {
				// name isn't available
				return false;
			}
		}
		// ID/name is available
		return true;
	}


	public int getGraphNodeIndex(String graphNodeName){
		int index = -1;
		for(int i=0; i<nodesArray.size(); i++) {
			if(((GraphNode)nodesArray.get(i)).getId()==graphNodeName)
			{
				index = i;
				break;
			}
		}
		//		System.out.println("Returning " + index);

		return index;
	}


}
