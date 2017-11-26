package edu.ucsc.cross.hse.core.model;

/*
 * A fundamental model the control information (header) of a packet that transports data through a network.
 */
public interface FundamentalPacketHeader<A>
{

	/*
	 * Returns the address of the source of the packet
	 */
	public A getSourceAddress();

	/*
	 * Returns the address of the destination of the packet
	 */
	public A getDestinationAddress();

	/*
	 * Returns the size of the payload in bytes
	 */
	public Integer getPayloadSize();
}
