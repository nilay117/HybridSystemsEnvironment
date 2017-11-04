package edu.ucsc.hsl.model.synchronization.distributednetwork;

import org.jgrapht.EdgeFactory;

public class Edge implements EdgeFactory<Integer, Edge>
{

	Integer a;
	Integer b;

	public Edge(Integer a, Integer b)
	{
		this.a = a;
		this.b = b;
	}

	@Override
	public Edge createEdge(Integer arg0, Integer arg1)
	{
		// TODO Auto-generated method stub
		return new Edge(arg0, arg1);
	}
}
