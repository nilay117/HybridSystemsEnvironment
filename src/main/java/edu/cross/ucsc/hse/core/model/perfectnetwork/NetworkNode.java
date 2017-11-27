package edu.cross.ucsc.hse.core.model.perfectnetwork;

import java.util.ArrayList;

public class NetworkNode<N>
{

	private ArrayList<Packet<PacketType>> receivedPackets;
	private ArrayList<Packet<PacketType>> transmissionPackets;
	public N nodeState;

	public NetworkNode(N node_state)
	{
		nodeState = node_state;
	}

	public N getNodeState()
	{
		return nodeState;
	}
}
