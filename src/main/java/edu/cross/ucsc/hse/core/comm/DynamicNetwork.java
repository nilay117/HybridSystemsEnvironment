package edu.cross.ucsc.hse.core.comm;

import org.jgrapht.graph.DirectedMultigraph;

public abstract class DynamicNetwork<N extends Node, L extends Link>
{

	protected DirectedMultigraph<N, L> network;
	private LinkFactory<N, L> linkFactory;

	public DynamicNetwork(LinkFactory<N, L> link_factory)
	{
		network = new DirectedMultigraph<N, L>(link_factory);
	}

}
