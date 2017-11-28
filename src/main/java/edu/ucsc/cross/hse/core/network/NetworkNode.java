package edu.ucsc.cross.hse.core.network;

import java.util.ArrayList;

public class NetworkNode<N> implements Node<N>
{

	private ArrayList<PacketHandler> packetHandlers;
	private ArrayList<Packet<?>> receivedPackets;
	private ArrayList<Packet<?>> transmissionPackets;
	public N nodeState;

	public NetworkNode(N node_state)
	{
		nodeState = node_state;
	}

	public N getNodeState()
	{
		return nodeState;
	}

	public Packet<?> handlePacket(Packet<?> packet)
	{
		for (PacketHandler packetHandler : packetHandlers)
		{
			if (packetHandler.canHandlePacket(this, packet))
			{

				return packetHandler.handlePacket(this, packet);
			}
		}
		return null;
	}

	public ArrayList<Packet<?>> receiveTransmissionPackets(ArrayList<Packet<?>> read_packets)
	{
		ArrayList<Packet<?>> handled = new ArrayList<Packet<?>>();
		for (Packet<?> packet : read_packets)
		{
			handled.add(handlePacket(packet));
		}
		receivedPackets.removeAll(handled);
		return handled;
	}

	public ArrayList<Packet<?>> getTransmissionPackets()
	{
		return transmissionPackets;
	}

	public void setTransmissionPackets(Packet<?>... transmission_packets)
	{
		for (Packet<?> packet : transmission_packets)
		{
			this.transmissionPackets.add(packet);
		}
	}

	@Override
	public void setTransmissionPackets(Packet<?> transmissionPackets)
	{
		// TODO Auto-generated method stub

	}
}
