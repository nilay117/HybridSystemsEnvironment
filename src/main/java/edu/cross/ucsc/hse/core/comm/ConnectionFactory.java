package edu.cross.ucsc.hse.core.comm;

import org.jgrapht.EdgeFactory;

public interface ConnectionFactory<N extends Node, L extends Link> extends EdgeFactory<N, L>
{

	public L createNode();

	L createEdge(N sourceVertex, N targetVertex);

}
