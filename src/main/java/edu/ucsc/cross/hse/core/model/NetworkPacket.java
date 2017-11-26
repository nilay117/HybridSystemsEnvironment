package edu.ucsc.cross.hse.core.model;

public interface NetworkPacket<H extends FundamentalPacketHeader<A>, P, A>
{

	public H getHeader();

	public P getPayload();

}