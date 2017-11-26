package edu.ucsc.cross.hse.core.network;

import edu.cross.ucsc.hse.core.comm.Link;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import java.util.List;
import org.jgrapht.graph.DirectedWeightedMultigraph;

public interface Network<N extends Node, L extends Link>
{

	public DirectedWeightedMultigraph<N, L> getConnectivityGraph();

	public N addNode(HybridSystem<?> system);

	public Object getAddress(N node);

	public List<Object> getReachableAddresses(N node);

	public List<Object> getConnectedAddresses(N node);

	public List<Packet> getPendingTransmisionQueue(N node);

	public List<Packet> getAdknowledgementReceivedPackets(N node);

	public List<Packet> getFailedPackets(N node);

	public List<Packet> getReceivedPackets(N node, boolean clear_queue);

}
