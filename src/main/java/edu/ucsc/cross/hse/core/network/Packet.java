package edu.ucsc.cross.hse.core.network;

public interface Packet
{

	public Object getSourceAddress();

	public Object getDestinationAddress();

	public Object getPayload();

	public Integer getPayloadSize();
}
