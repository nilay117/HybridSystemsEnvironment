package edu.cross.ucsc.hse.core.nnn;

public class NetworkNode<N>
{

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
