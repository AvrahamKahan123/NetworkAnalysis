package graph.network;

import java.util.ArrayList;
import java.util.Collections;
import graph.CapGraph;
import graph.Vertex;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;



/**
 * @author Avraham Kahan
 *
 */

public final class NetStats {
	
	private NetStats() {
		return; // can't be called: isn't logical since NetStats is meant to be basically a static class
	}
	
	public static void writeNetStats(Network net) throws IOException{
		    String netStatsString = getStatsString(net);
			String fileName = "../../data/citNetStats/netStats" + System.currentTimeMillis();
		    BufferedWriter statsWriter = new BufferedWriter(new FileWriter(fileName));	
		    statsWriter.write(netStatsString);
		    statsWriter.close();
	}

	public static void printNetStats(Network net) {
		System.out.println(getStatsString(net));
	}
	
	public static void printNetStats(ArrayList<Vertex> vertices) {
		Network net = (Network) CapGraph.convertVerticesToNet(vertices);
		printNetStats(net);
	}
	
	private static String getStatsString(Network net) {
		float edgesPerVertex = getEdgesPerVertex(net);
		float iqRange = getInterquartilerRange(net);
		float standardDeviation = getStdDeviation(net);
		float range = edgesRange(net);
		String statAnalysis = "";
		statAnalysis+="The number of Edges per vertex is: " +  Float.toString(edgesPerVertex);
		statAnalysis+="\nThe interquartile (between 25th and 75th percentile) range of incoming edges is: " + Float.toString(iqRange);
		statAnalysis+="\nThe Standard Deviation of #edges per node is: " + Float.toString(standardDeviation);
		statAnalysis+="\nThe Range of #edges per node is: " + Float.toString(range);
		statAnalysis+="\nThe total strength score of the given network is: " + Float.toString(calcNetStrength(edgesPerVertex, standardDeviation, iqRange, range));
		return statAnalysis;
	}
	
	public static float getEdgesPerVertex(Network net) {
		return (net.getNumEdges()/net.getAllVertices().size());
	}
	
	public static float getStdDeviation(Network net) {
		ArrayList<Integer> numEdges = new ArrayList<Integer>();
		for (Vertex node: net.getAllVertices()) {
			numEdges.add(node.getParentNodes().size()); //parent nodes are used so we're always looking at the person being cited
		}
		return StatMeasures.getStdDev(numEdges); 
	}
	
	public static float getInterquartilerRange(Network net) {
		ArrayList<Integer> numEdges = new ArrayList<Integer>();
		for (Vertex node: net.getAllVertices()) {
			numEdges.add(node.getParentNodes().size()); //parent nodes are used so we're always looking at the person being cited
		}
		return StatMeasures.getIqRange(numEdges); 
	}
	
	public static int edgesRange(Network net) {
		ArrayList<Integer> numEdges = new ArrayList<Integer>();
		for (Vertex node: net.getAllVertices()) {
			numEdges.add(node.getParentNodes().size()); //parent nodes are used so we're always looking at the person being cited
		}
		Collections.sort(numEdges);
		return numEdges.get(numEdges.size() - 1) - numEdges.get(0);
	}
	
	public static float calcNetStrength(float edgesPerVertex, float stdDev, float iqRange, float range) { // 
		// my own custom metric for measuring network strength 
		return (iqRange/range)*edgesPerVertex - stdDev * 0.1f ; // just a sort of guessed metric on how well connected different nodes are
	}
	
}

