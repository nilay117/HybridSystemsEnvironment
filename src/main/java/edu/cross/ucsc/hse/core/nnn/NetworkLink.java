package edu.cross.ucsc.hse.core.nnn;

public class NetworkLink<N>
{

	public NetworkNode<N> origin;
	public NetworkNode<N> destination;

	public NetworkLink(NetworkNode<N> org, NetworkNode<N> dest)
	{
		this.origin = org;
		this.destination = dest;
	}

}
