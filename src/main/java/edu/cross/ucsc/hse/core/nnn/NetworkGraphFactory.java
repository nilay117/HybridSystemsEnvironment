package edu.cross.ucsc.hse.core.nnn;

import org.jgrapht.EdgeFactory;

public class NetworkGraphFactory<N> implements EdgeFactory<NetworkNode<N>, NetworkLink<N>>
{

	NetworkTopology<N> network;

	@Override
	public NetworkLink<N> createEdge(NetworkNode<N> arg0, NetworkNode<N> arg1)
	{
		// TODO Auto-generated method stub
		return new NetworkLink<N>(network, arg0, arg1);
	}

	public NetworkGraphFactory(NetworkTopology<N> network)
	{
		this.network = network;
	}

}