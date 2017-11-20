package edu.ucsc.cross.hse.core.graph;

public class NetworkEdge
{

	Integer origin;

	public Integer getOrigin()
	{
		return origin;
	}

	public Integer getDestination()
	{
		return destination;
	}

	Integer destination;

	public NetworkEdge(Integer a, Integer b)
	{
		this.origin = a;
		this.destination = b;
	}
}
