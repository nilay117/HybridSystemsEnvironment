package edu.cross.ucsc.hse.core.comm;

public interface Packet<P>
{

	public PacketHeader getHeader();

	public P getPayload();
}
