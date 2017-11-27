package edu.ucsc.cross.hse.core.netG;

public class LinkedAddresses
{

	NetworkAddress origin;

	public NetworkAddress getOrigin()
	{
		return origin;
	}

	public NetworkAddress getDestination()
	{
		return destination;
	}

	NetworkAddress destination;

	public LinkedAddresses(NetworkAddress a, NetworkAddress b)
	{
		this.origin = a;
		this.destination = b;
	}
}
