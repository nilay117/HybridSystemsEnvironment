package edu.cross.ucsc.hse.core.comm;

public interface Packet<P>
{

	public Object getSourceAddress();

	public Object getDestinationAddress();

	public P getPayload();

}
