package edu.ucsc.cross.hse.models.network.syncconsensus;

import edu.ucsc.cross.hse.core.object.Objects;
import java.util.ArrayList;

public class NetworkState extends Objects
{

	public ArrayList<NodeState> nodes;
	public double eventTimer;

	public NetworkState(ArrayList<NodeState> nodez, double event_timer)
	{
		nodes = nodez;
		eventTimer = event_timer;
	}

	public Integer totalNodes()
	{
		return nodes.size();
	}
}
