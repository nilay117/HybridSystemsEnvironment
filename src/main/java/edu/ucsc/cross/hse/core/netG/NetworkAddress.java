package edu.ucsc.cross.hse.core.netG;

public class NetworkAddress
{

	public static Integer count = 0;
	private Integer address;

	public NetworkAddress()
	{
		address = count++;
	}
}
