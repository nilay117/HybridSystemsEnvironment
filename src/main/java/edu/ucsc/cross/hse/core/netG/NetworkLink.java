package edu.ucsc.cross.hse.core.netG;

public class NetworkLink<N>
{

	public N origin;
	public N destination;

	public NetworkLink(N org, N dest)
	{
		this.origin = org;
		this.destination = dest;
	}
}
