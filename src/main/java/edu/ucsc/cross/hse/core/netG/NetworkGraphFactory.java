package edu.ucsc.cross.hse.core.netG;

import org.jgrapht.EdgeFactory;

public class NetworkGraphFactory<N> implements EdgeFactory<N, NetworkLink<N>>
{

	@Override
	public NetworkLink<N> createEdge(N arg0, N arg1)
	{
		// TODO Auto-generated method stub
		return new NetworkLink<N>(arg0, arg1);
	}

	public NetworkGraphFactory()
	{

	}

}