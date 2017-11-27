package edu.cross.ucsc.hse.core.model.perfectnetwork;

public class NetworkLink<V extends NetworkNode<N>, N> implements Link<V, N>
{

	private V origin;
	private V destination;

	public V getOrigin()
	{
		return origin;
	}

	public V getDestination()
	{
		return destination;
	}

	public NetworkLink(V org, V dest)
	{
		this.origin = org;
		this.destination = dest;
	}

}
