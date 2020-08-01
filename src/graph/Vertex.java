package graph;

import java.util.ArrayList;


/**
 * @author Avraham Kahan
 *
 */

/*
 * Vertex represents nodes in a non-hierarchial tree
 */
public class Vertex {
	int id;
	ArrayList<Vertex> parentNodes = new ArrayList<Vertex>();
	ArrayList<Vertex> childNodes = new ArrayList<Vertex>();
	
	public Vertex(int id) {
		this.id = id;
	}
	
	public Vertex(int id, ArrayList<Vertex> parentNodes, ArrayList<Vertex> childNodes) {
		this.id = id;
		this.parentNodes = parentNodes;
		this.childNodes = childNodes;
	}
	public int getId() {
		return this.id;
	}

	public ArrayList<Vertex> getChildNodes() {
		return childNodes;
	}

	public void addChildNode(Vertex newChildNode) {
		childNodes.add(newChildNode);
	}
	
	public void setChildNodes(ArrayList<Vertex> newChildNodes) {
		childNodes = new ArrayList<Vertex>(newChildNodes);
	}
	
	public ArrayList<Vertex> getParentNodes() {
		return parentNodes;
	}

	public void addParentNode(Vertex newParentNode) {
		parentNodes.add(newParentNode);
	}
	
	public void setParentNodes(ArrayList<Vertex> newParentNodes) {
		parentNodes = new ArrayList<Vertex>(newParentNodes);
	}
	
	public ArrayList<Vertex> getAllNeighbors() {
		ArrayList<Vertex> ret = new ArrayList<Vertex>(childNodes);
		ret.addAll(parentNodes);
		return ret;
	}
	
	public boolean connectsTo(Vertex potentialNeighbor) {
		for (Vertex neighbor: childNodes) {
			if (neighbor.getId() == potentialNeighbor.getId()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(Vertex otherVertex) {
		return id == otherVertex.getId();
	}
	public String toString() {
		return Integer.toString(this.id);
	}

}
