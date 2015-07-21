package pige.dataLayer;

import java.util.LinkedList;


public class GraphNodeFactory {

	public static GraphNode createGraphNode(double positionXInput,
			double positionYInput, String idInput, String nameInput,
			double nameOffsetXInput, double nameOffsetYInput
			) {
		return new GraphNode(positionXInput, positionYInput, idInput, nameInput,
				nameOffsetXInput, nameOffsetYInput);
	}


	public static GraphNode createGraphNode(double positionXInput, double positionYInput) {
		return new GraphNode(positionXInput, positionYInput);
	}

}
