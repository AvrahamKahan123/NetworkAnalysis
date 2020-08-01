package graph.network;

import java.util.ArrayList;
import java.util.HashSet;
import graph.Vertex;



/**
 * @author Avraham Kahan
 *
 */


public class Community {
	int id;
	HashSet<Vertex> vertices;
	ArrayList<Edge> allEdges;
	
	public Community(Vertex node) {  // every community starts as community of a single node
		vertices = new HashSet<Vertex>();
		allEdges = new ArrayList<Edge>();
		vertices.add(node);
		id = node.getId();
	}
	
	public void addEdge(Edge newEdge) {
		if (!allEdges.contains(newEdge)) {
			allEdges.add(newEdge);
		}
	}
	
	public ArrayList<Edge> getEdges() {
		return allEdges;
	}
	
	public HashSet<Vertex> getVertices() {
		return vertices;
	}
	
	private String printL(HashSet<Vertex> a) {
		String ret = "";
		for (Vertex el: a) {
			ret += el + " ";
		}
		return ret;
	}
	
	
	// merges other communities vertices and edges into its own
	public void merge(Community otherCommunity) {
		System.out.println("Merged nodes " + printL(otherCommunity.getVertices()) + "      " + printL(this.getVertices()));
		vertices.addAll(otherCommunity.getVertices());
		ArrayList<Edge> otherEdges = otherCommunity.getEdges();
		Edge currentEdge;
		for (int i = 0; i < otherEdges.size(); i++) { // would use for each loop, except this does not allow modification since it uses an iterator
			currentEdge = otherEdges.get(i);
			currentEdge.changeConnecting(this, otherCommunity);
			allEdges.add(currentEdge);
		}
	}
	
	public void removeDuplicateEdges() {
		allEdges.removeIf(e -> e.pointsToSelf());
	}

	
	// find edges that point from a community to itself
	public HashSet<Edge> findSelfLoops() { 
		HashSet<Edge> selfLoops = new HashSet<Edge>();
		for(Edge currentEdge: allEdges) {
			if (currentEdge.pointsToSelf()) {
				selfLoops.add(currentEdge);
			}
		}
		return selfLoops;
	}
	
	public String toString() {
		String ret = " ";
		for (Vertex node: vertices) {
			ret += node.toString() +" ";
		}
		return ret;
	}
	
	public int getId() {
		return this.id;
	}
	
}
