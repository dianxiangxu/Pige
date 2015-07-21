package pige.dataLayer;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;

public interface GraphDataLayerInterface {

	public abstract GraphDataLayerInterface clone();

	public abstract void addArc(GraphArc arcInput);

	public abstract void addGraphObject(GraphObject pnObject);

	public abstract void removeGraphObject(GraphObject pnObject);

	public abstract Iterator getGraphObjects();

	public abstract boolean hasGraphObjects();

	public abstract GraphNode[] getGraphNodes();

	public abstract ArrayList<GraphNode> getGraphNodesArrayList();

	public abstract int getGraphNodesCount();

	public abstract GraphAnnotationNote[] getLabels();

	public abstract GraphArc[] getArcs();

	public abstract String getUniqueArcId();

	public abstract ArrayList<GraphArc> getArcsArrayList();

	public abstract GraphNode getGraphNodeById(String graphNodeID);

	public abstract GraphNode getGraphNodeByName(String graphNodeName);

	public abstract GraphNode getGraphNode(int graphNodeNo);

	public abstract GraphAbstractNode getAbstractNodeObject(String ptoId);

	public abstract void createFromXML(Document PNMLDoc);

	public abstract String getURI();

	public abstract void print();

	public abstract boolean checkGraphNodeIDAvailability(String newName);

	public abstract int getGraphNodeIndex(String placeName);


}