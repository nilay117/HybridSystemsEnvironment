package edu.ucsc.cross.hse.core.network;

public class NetworkLink<N> // implements Link<NetworkNode<N>, NetworkLink<N>>
{

	private NetworkNode<N> origin;
	private NetworkNode<N> destination;

	public NetworkNode<N> getOrigin()
	{
		return origin;
	}

	public NetworkNode<N> getDestination()
	{
		return destination;
	}

	public NetworkLink(NetworkNode<N> org, NetworkNode<N> dest)
	{
		this.origin = org;
		this.destination = dest;
	}

}
