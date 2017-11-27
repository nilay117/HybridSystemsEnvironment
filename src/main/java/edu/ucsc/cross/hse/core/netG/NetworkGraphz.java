package edu.ucsc.cross.hse.core.netG;

import org.jgrapht.graph.DirectedPseudograph;

public class NetworkGraphz<Object>
{

	DirectedPseudograph<N, NetworkLink<N>> graph;

	public NetworkGraphz()
	{
		graph = new DirectedPseudograph<N, NetworkLink<N>>(new NetworkGraphFactory<N>());
	}

	public static void main(String args[])
	{
		NetworkGraphz<String> s = new NetworkGraphz<String>();
		s.graph.addVertex("yo");
		s.graph.addVertex("word");
		s.graph.addEdge("yo", "word");
		System.out.println(s.graph.edgesOf("yo").iterator().next());
	}
}
