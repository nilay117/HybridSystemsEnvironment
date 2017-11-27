package edu.cross.ucsc.hse.core.nnn;

import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.DirectedPseudograph;

public class NetworkTopology<N, L>
{

	DirectedPseudograph<N, L> graphStructure;

	public public NetworkTopology()
	{
		graph = new DirectedMultigraph<NetworkNode<N>, NetworkLink<N>>(new NetworkGraphFactory(this));
	}

	public <D> void connect(NetworkNode<S> source, NetworkNode<D> dest)
	{
		NetworkLink newLink = new NetworkLink<S, D>(this, source, dest);
		graph.addVertex(source);
		graph.addVertex(dest);
		graph.addEdge(source, dest, newLink);
	}

}
