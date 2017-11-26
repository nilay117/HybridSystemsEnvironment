package edu.ucsc.cross.hse.core.network;

import edu.ucsc.cross.hse.core.object.HybridSystem;
import java.util.Set;

public abstract class CommunicationNetwork<S, N, L> extends HybridSystem<S>
{

	public CommunicationNetwork(S state)
	{
		super(state);
		// TODO Auto-generated constructor stub
	}

	public Set<N> getConnectedNodes(N node);

	public Set<N> getReachableNodes(N node);

	public Set<N> getAllNodes();
}
