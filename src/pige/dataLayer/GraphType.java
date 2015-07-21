package pige.dataLayer;

public class GraphType {

	private String nodeTitle;
	private String arcTitle;
	private String nodeIdPrefix;
	private String arcIdPrefix;
	private boolean hasNodeProperty;
	private boolean hasArcProperty;
	private boolean allowsSelfArc;
	private boolean allowsMultipleArcs;
	
	public GraphType(String nodeTitle, String arcTitle, 
			String nodeIdPrefix, String arcIdPrefix, 
			boolean hasNodeProperty, boolean hasArcProperty,
			boolean allowsSelfArc, boolean allowsMultipleArcs){
		this.nodeTitle = nodeTitle;
		this.arcTitle = arcTitle;
		this.nodeIdPrefix = nodeIdPrefix;
		this.arcIdPrefix = arcIdPrefix;
		this.hasNodeProperty = hasNodeProperty;
		this.hasArcProperty = hasArcProperty;
		this.allowsSelfArc = allowsSelfArc;
		this.allowsMultipleArcs = allowsMultipleArcs;
	} 

	public static GraphType FiniteStateMachine = 
		new GraphType("State",
				"Transition",
				"S",
				"T", 
				false, 	// no node property - just state name
				true, 	// arc property include event, precond, postcond
				true,	// arcs on the same state node are allowed	
				true	// multiple arcs between two nodes are allowed
				);	
	
	public static GraphType ThreatTree =
		new GraphType("Node", 
				"Arc", 
				"N", 
				"A", 
				true, 	// node property includes logical relation of siblings
				false, 	// no arc label is needed
				false,	// arcs on the same node are not allowed
				false	// multiple arcs between two nodes are not allowed
				);	
	
	public String getNodeTitle(){
		return nodeTitle;
	}
	
	public String getNodeIdPrefix(){
		return nodeIdPrefix;
	}

	public boolean hasNodeProperty(){
		return hasNodeProperty;
	}

	public String getArcTitle(){
		return arcTitle;
	}
	
	public String getArcIdPrefix(){
		return arcIdPrefix;
	}

	public boolean hasArcProperty(){
		return hasArcProperty;
	}

	public boolean allowsSelfArc(){
		return allowsSelfArc;
	}

	public boolean allowsMultiplefArcs(){
		return allowsMultipleArcs;
	}

}
