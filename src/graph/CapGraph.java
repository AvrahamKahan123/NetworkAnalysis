package graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;


/**
 * @author Avraham Kahan
 *
 */
public class CapGraph implements Graph {

	HashMap<Integer, Vertex> vertices = new HashMap<Integer, Vertex>();
	/* (non-Javadoc)
	 * @see graph.Graph#addVertex(int)
	 */
	@Override
	public void addVertex(int num) {
		if (vertices.containsKey(num)) {
			//System.out.println("Attempted to add Vertex with ID: " + num + ",  which already exists");
			return;
		}
		Vertex newVertex = new Vertex(num);
		this.vertices.put(num, newVertex);
	}
	
	public void addVertex(Vertex newVertex) { //useful for when computing transpose of graph
		if (vertices.containsKey(newVertex.getId())) {
			System.out.println("Attempted to add Vertex with ID: " + newVertex.getId() + ",  which already exists");
			return;
		}
		this.vertices.put(newVertex.getId(), newVertex);
	}
	
	public Vertex getVertex(int num) {
		return this.vertices.get(num);
	}

	public ArrayList<Vertex> getAllVertices() {
		return new ArrayList<Vertex>(this.vertices.values());
	}
	
	public ArrayList<Integer> getAllVertexIds() {
		return new ArrayList<Integer>(this.vertices.keySet());
	}
	
	
	// copies Vertices, doesn't simply point to old list of vertices
	public void setVertices(ArrayList<Vertex> originalVertices) {
		for (Vertex originalNode: originalVertices) {
			addVertex(originalNode.getId());
		}
	}
	

	/* (non-Javadoc)
	 * @see graph.Graph#addEdge(int, int)
	 */
	@Override
	public void addEdge(int from, int to) {
		if (!(this.vertices.containsKey(from) && this.vertices.containsKey(to))) {
			System.out.println("Attemtpted to add edge to Node/s which do not exist in graph");
			return;
		}
		vertices.get(from).addChildNode(vertices.get(to));
		vertices.get(to).addParentNode(vertices.get(from));
	}
	
	public CapGraph getTranspose() {
		CapGraph transpose = new CapGraph();
		for (Vertex node: vertices.values()) {
			transpose.addVertex(node.getId());
		}
		for (Vertex node: vertices.values()) {
			for (Vertex childNode: node.getChildNodes()) {
				transpose.addEdge(childNode.getId(), node.getId());
			}
		}
		return transpose;
	}
	
	
	// return CapGraph consisting of all vertices in list netVertices and all their connections
	public static CapGraph convertVerticesToNet(ArrayList<Vertex> netVertices) {
		CapGraph net = new CapGraph();
		HashSet<Integer> allNodes = nodesToIds(netVertices);
		for (Vertex node: netVertices) {
			net.addVertex(node.getId());
		}
		for (Vertex node: netVertices) {
			for (Vertex child: node.getChildNodes()) {
				if (allNodes.contains(child.getId())) {
					net.addEdge(node.getId(), child.getId());
				}
			}
		}
		return net;
	}
	
	private static HashSet<Integer> nodesToIds(ArrayList<Vertex> vertices) {
		HashSet<Integer> ids = new HashSet<Integer>();
		for (Vertex node: vertices) {
			ids.add(node.getId());
		}
		return ids;
	}


	/* (non-Javadoc)
	 * @see graph.Graph#getEgonet(int)
	 * returns egonet for vertex identified by its integer id
	 */
	@Override
	public Graph getEgonet(int center) { 
		Vertex centerNode = vertices.get(center);
		if (centerNode == null) {
			System.out.println("Egonet was requested for non existent node");
			return new CapGraph();
		}
		// get all nodes connected to egonet center
		CapGraph egonet = new CapGraph(); // egonet to be returned 
		HashSet<Vertex> connectedNodes = new HashSet<Vertex>(centerNode.getAllNeighbors()); // nodes that will be in the egonet graph
		connectedNodes.add(centerNode); // center is needed so that the graph can be reconstructed later
		egonet.setVertices(centerNode.getAllNeighbors());
		// get all edges connecting between relevant nodes and add them to graph
		egonet.addVertex(center);
		for (Vertex connectedNode: connectedNodes) {
			for (Vertex child: connectedNode.getChildNodes()) {
				if (connectedNodes.contains(child)) {
					egonet.addEdge(connectedNode.getId(), child.getId());
				}
			}
		}
		return egonet;
	}
	

	/* Finds strongly connected components in the graph
	 * (non-Javadoc)
	 * @see graph.Graph#getSCCs()
	 * Implements Kosaraju’s algorithm for finding strongly connected components in a graph
	 */
	@Override
	public List<Graph> getSCCs() {
		LinkedList<Vertex> firstDFS = new LinkedList<Vertex>(DFS(this, new LinkedList<Vertex>(this.vertices.values()), false));
		CapGraph transpose = getTranspose();
		LinkedList<Vertex> transposedFirstDFS = new LinkedList<Vertex>();
        for (Vertex node: firstDFS) { // re-referencing first DFS search stack vertices to transpose of graph, which uses different vertex objects with identical IDs
            transposedFirstDFS.add(0, transpose.getVertex(node.getId())); 
        }
        Stack<Vertex> secondDFS = DFS(transpose, transposedFirstDFS, true);
        return interpretSecondDFS(secondDFS);
	}

	
	// reads through the ordering created by the second DFS and returns the SCCs based on it
	private List<Graph> interpretSecondDFS(Stack<Vertex> secondDFS) {
		 ArrayList<Vertex> currentGraphVertices = new ArrayList<Vertex>();
			ArrayList<Graph> SCCs= new ArrayList<Graph>();
			for (int i = 0; i < secondDFS.size(); i++) {
				if (secondDFS.get(i) == null) {
					SCCs.add(createSCCGraph(currentGraphVertices));
					currentGraphVertices.clear();
				}
				else {
					currentGraphVertices.add(secondDFS.get(i));
				}
			}
			return SCCs;
	}
	
	
	// split by root will split the list, by inserting null at the root of every subtree explored
	// returns stack, where bottom value is the first traversed
	private Stack<Vertex> DFS(CapGraph graph, LinkedList<Vertex> allVertices, boolean splitByRoot) { 
		HashSet<Vertex> visited = new HashSet<Vertex>();
		Stack<Vertex> finished = new Stack<Vertex>();
		Vertex curVertex; // to track current vertex in depth first search
		while ((allVertices.size()) > 0) {
			curVertex = allVertices.pop();
			if (!(visited.contains(curVertex))) {
				DFSvisit(graph, curVertex, visited, finished);
				if (splitByRoot) {
					finished.add(null);
				}
			}	
		}
		return finished;
	}
	
	private void DFSvisit(CapGraph graph, Vertex curVertex, HashSet<Vertex> visited, Stack<Vertex> finished) {
		visited.add(curVertex);
		for (Vertex neighbor: curVertex.getChildNodes()) {
			if (!(visited.contains(neighbor))) {
				DFSvisit(graph, neighbor, visited, finished);
			}
		}
		finished.push(curVertex);
	}
	
	// creates Graphs with all vertices, and then computes the transpose
	private CapGraph createSCCGraph(ArrayList<Vertex> currentGraphVertices) {
		CapGraph SCC = new CapGraph();
		for (Vertex curVertex: currentGraphVertices) {
			SCC.addVertex(curVertex.getId());
		}
		return SCC.getTranspose();
	}

	
	/* (non-Javadoc)
	 * @see graph.Graph#exportGraph()
	 */
	@Override
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
		HashMap<Integer, HashSet<Integer>> graphConnections = new HashMap<Integer, HashSet<Integer>>();
		for (Vertex node: this.vertices.values()) {
			graphConnections.put(node.getId(), new HashSet<Integer>());
			for (Vertex child: node.getChildNodes()) {
				graphConnections.get(node.getId()).add(child.getId());
			}
		}
		return graphConnections;
	}
	
	public String toString() {
		return exportGraph().toString();
	}
	
    public static void main(String[] args) {
    	CapGraph a = new CapGraph();
    	a.addVertex(1);
    	a.addVertex(2);
    	a.addVertex(0);
    	a.addVertex(3);
    	a.addVertex(4);
    	a.addEdge(1, 0);
    	a.addEdge(0, 2);
    	a.addEdge(0, 3);
    	a.addEdge(3, 4);
    	a.addEdge(2, 1);
    	System.out.println(a.getSCCs());
    	//List<Graph> b = a.getSCCs();
    	//for (Graph c: b) {
    		//System.out.println(c.exportGraph());
    	//}
   
    }

}
