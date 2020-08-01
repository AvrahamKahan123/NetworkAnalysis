package graph.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;
import graph.Vertex;



/**
 * @author Avraham Kahan
 *
 */

public class Network extends graph.CapGraph {
	private int numEdges;
	Random randomEngine;
	
	public Network() {} 
	
	public Network(String networkFile) {
		randomEngine = new Random();
		ArrayList<Integer[]> edges = parseNetworkFile(networkFile);
		for (Integer[] edge: edges) {
			addVertex(edge[0]);
			addVertex(edge[1]);
			addEdge(edge[0], edge[1]);
		}
	}
	
	//calling this method makes the graph undirected by making child nodes parent nodes of their parents, and visa versa
	public void undirectGraph() {
		for (Vertex node: this.getAllVertices()) {
			for (Vertex childNode: node.getChildNodes()) {
				node.addParentNode(childNode);
				childNode.addChildNode(node);
			}
		}
	}
	
	
	/*
	 * Community detection algorithm using Karger's Algorithm (Monte Carlo method for discovering communities in O(V^2) time), where V is vertices
	 * For paper describing algorithm, see http://people.csail.mit.edu/karger/Papers/mincut.ps
	 * Finds numCommunities communities
	 */
	public HashSet<Community> getCommunities(int numCommunities) {
		HashSet<Edge> allEdges = new HashSet<Edge>(); 
		HashSet<Community> allCommunities = initializeCommunities(super.getAllVertices(), allEdges);
		Edge randomEdge;
		Community[] connectedCommunities;
		while (allCommunities.size() > numCommunities) {
			randomEdge = popRandomElement(allEdges);  
			System.out.println(randomEdge.id);
			connectedCommunities = randomEdge.getNodes();

			allCommunities.remove(connectedCommunities[1]);
			connectedCommunities[0].merge(connectedCommunities[1]);
			removeEdges(allEdges, connectedCommunities[0].findSelfLoops());
			connectedCommunities[0].removeDuplicateEdges();
		}
		return allCommunities;
	}
	
	
	//passed tests
	private HashSet<Community> initializeCommunities(ArrayList<Vertex> allVertices, HashSet<Edge> allEdges) {
		HashSet<Community> ret = new HashSet<Community>();
		HashMap<Vertex, Community> intialCommunityIds = new HashMap<Vertex, Community>(); // to obtain intial edges for communities
		Community newestCommunity;
		for (Vertex node: allVertices) {
			newestCommunity = new Community(node);
			ret.add(newestCommunity);
			intialCommunityIds.put(node, newestCommunity);
		}
		linkCommunities(allVertices, intialCommunityIds, allEdges);
		return ret;
	}
	
	
	//passed tests
	private void linkCommunities(ArrayList<Vertex> vertices, HashMap<Vertex, Community> intialCommunityIds, HashSet<Edge> allEdges) {
		Community parentCommunity, childCommunity;
		Edge newestEdge;
		for (Vertex node: vertices) {
			for(Vertex childnode: node.getChildNodes()) {
				parentCommunity = intialCommunityIds.get(node);
				childCommunity = intialCommunityIds.get(childnode);
				newestEdge = new Edge(parentCommunity, childCommunity);
				parentCommunity.addEdge(newestEdge);
				childCommunity.addEdge(newestEdge);
				allEdges.add(newestEdge);
			}
		}
	}
	
	private void removeEdges(HashSet<Edge> allEdges, HashSet<Edge> duplicateEdges) {
		for (Edge currentEdge: duplicateEdges) {
			allEdges.remove(currentEdge);
		}
	}
	
	
	// algorithm needs to be optimized currently runs in O(n) time, where n = size of allEdges
	private Edge popRandomElement(HashSet<Edge> allEdges) {
		int size = allEdges.size();
		int item = randomEngine.nextInt(size); 
		int i = 0;
		for(Edge currentEdge : allEdges)
		{
		    if (i == item) {
		    	allEdges.remove(currentEdge);
		    	return currentEdge;
		    }
		    i++;
		}
		return new Edge(); // should never execute
	}
	
	
	
	//passed tests
	// parses file containing citations of the form "node1 node2"
	private ArrayList<Integer[]> parseNetworkFile(String networkFilePath) {
		ArrayList<Integer[]> allEdges = new ArrayList<Integer[]>();
		String[] nodes;
		//Integer[] currentCitation = new Integer[2];
		Pattern properLine = Pattern.compile("\\d+\\s+\\d+");
		try {
			File citationFile = new File(networkFilePath);
			Scanner fileReader = new Scanner(citationFile);
			while (fileReader.hasNextLine()) {
				Integer[] currentEdge = new Integer[2];
				String edge = fileReader.nextLine().strip();
				if (!properLine.matcher(edge).matches()) {
					continue;
				}
				nodes = edge.split("\\s+");
				currentEdge[0] = Integer.parseInt(nodes[0]);
				currentEdge[1] = Integer.parseInt(nodes[1]);
				allEdges.add(currentEdge);
			}
			fileReader.close();
		}
		catch (FileNotFoundException e){
			System.out.println("Path of input file not found"); // will be changed to exception
			System.exit(-1); 
		}
		return allEdges;
	}
	
	public void addEdge(int from, int to) {
		super.addEdge(from, to);
		numEdges+=1;
	}
	
	public int getNumEdges() {
		return numEdges;
	}
	
	
	public static void main(String[] args) {
		Network kargerTest = new Network("/home/avraham/eclipse-workspace/Networks/data/kargerTest/test1.txt");
		HashSet<Community> communities = kargerTest.getCommunities(2);
		for (Community com: communities) {
			System.out.println("community is " + com);
		}
	}
}
