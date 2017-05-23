package edu.ucsc.cross.hse.core.object.accessors;

public class Address
{

	protected Integer localAddress; // local address of the element ie with
									// respect to other elements of the
									// same type

	protected Integer globalAddress; // global address of the element ie with
										// respect to the environment

	public Address(Integer local_address, Integer global_address)
	{

	}

	public Integer getLocalAddress()
	{
		return localAddress;
	}

	public Integer getGlobalAddress()
	{
		return globalAddress;
	}

}
