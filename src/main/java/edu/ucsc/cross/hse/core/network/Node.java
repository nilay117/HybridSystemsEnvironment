package edu.ucsc.cross.hse.core.network;

import java.util.List;

public interface Node
{

	public Object getAddress();

	public List<Object> getReachableAddresses();

	public List<Object> getConnectedAddresses();

	public List<Packet> getTransmissionQueue();

	public List<Packet> getSuccessfulPackets();

	public List<Packet> getFailedPackets();

	public List<Packet> getReceivedPackets(boolean clear_queue);

}
