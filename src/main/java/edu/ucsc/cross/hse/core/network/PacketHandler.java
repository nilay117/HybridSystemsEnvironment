package edu.ucsc.cross.hse.core.network;

public interface PacketHandler
{

	public <N> boolean canHandlePacket(NetworkNode<N> node, Packet<?> packet);

	public <N> Packet<?> handlePacket(NetworkNode<N> node, Packet<?> packet);
}
