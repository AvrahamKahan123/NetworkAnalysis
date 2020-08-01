package graph.network;




/**
 * @author Avraham Kahan
 *
 */


public class Edge {
	Community[] connectedCommunities= new Community[2];
	public Edge() {}; // default constructor
	public int id;
	
	//perhaps connected communities should be made a HashSet which would take care of self loops for us
	public Edge(Community community1, Community community2) {
		connectedCommunities[0] = community1;
		connectedCommunities[1] = community2;
		id = community1.getId() * 10 + community2.getId();
	}
	
	public void addEdge(Community newCommunity) {
		int i = connectedCommunities[0] == null ? 0 : 1;
		connectedCommunities[i] = newCommunity;
	}
	
	public Community[] getNodes() {
		return connectedCommunities;
	}
	
	public void setVertex(Community newCommunity, int index) {
		connectedCommunities[index] = newCommunity;
	}
	
	public boolean equals(Edge otherEdge) {
		Community[] connectingOther = otherEdge.getNodes();
		for (int i = 0; i < 2; i++) {
			if (connectingOther[i] == connectedCommunities[0] && connectingOther[(i+1) % 2] == connectedCommunities[1]) {
				return true;
			}
		}
		return false;
	}
	
	public boolean pointsToSelf() {
		return connectedCommunities[0] == connectedCommunities[1];
	}

	
	public void changeConnecting(Community newCommunity, Community oldCommunity) {
		boolean set = false;
		for (int i = 0; i < 2; i++) {
			if (connectedCommunities[i].equals(oldCommunity)) {
				connectedCommunities[i] = newCommunity;
				set = true;
				break;
			}
		}
		if (!set) {
			System.out.println("Well ill be darned");
		}
	}

	
}
