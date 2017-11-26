package edu.cross.ucsc.hse.core.comm;

import org.jgrapht.graph.DirectedWeightedMultigraph;

public interface Network
{

	DirectedWeightedMultigraph<Node, Link> getTopology();
}
