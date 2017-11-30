package edu.ucsc.cross.hse.core.network;

import org.jgrapht.EdgeFactory;

public class LinkFactory<N> implements EdgeFactory<NetworkNode<N>, NetworkLink<N>>
{

	@Override
	public NetworkLink<N> createEdge(NetworkNode<N> sourceVertex, NetworkNode<N> targetVertex)
	{
		// TODO Auto-generated method stub
		return new NetworkLink<N>(sourceVertex, targetVertex);
	}

}
