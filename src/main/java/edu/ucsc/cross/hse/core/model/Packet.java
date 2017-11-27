package edu.ucsc.cross.hse.core.model;

public interface Packet<P>
{

	public PacketHeader getHeader();

	public P getPayload();
}
