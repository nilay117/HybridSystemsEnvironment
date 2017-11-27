package edu.cross.ucsc.hse.core.model.perfectnetwork;

public interface Node<N>
{

	public void respondToPacket(Packet<PacketType> packet);

}
