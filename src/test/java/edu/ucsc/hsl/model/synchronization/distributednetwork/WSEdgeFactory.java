package edu.ucsc.hsl.model.synchronization.distributednetwork;

import org.jgrapht.EdgeFactory;

public class WSEdgeFactory implements EdgeFactory<Integer, Edge>
{

	@Override
	public Edge createEdge(Integer arg0, Integer arg1)
	{
		// TODO Auto-generated method stub
		return new Edge(arg0, arg1);
	}

}
