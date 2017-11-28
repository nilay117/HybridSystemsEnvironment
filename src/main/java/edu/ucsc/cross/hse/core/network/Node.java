package edu.ucsc.cross.hse.core.network;

import java.util.ArrayList;

public interface Node<N>
{

	public N getNodeState();

	public Packet<?> handlePacket(Packet<?> packet);

	public ArrayList<Packet<?>> receiveTransmissionPackets(ArrayList<Packet<?>> read_packets);

	public ArrayList<Packet<?>> getTransmissionPackets();

	public void setTransmissionPackets(Packet<?> transmissionPackets);

}
