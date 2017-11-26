package edu.cross.ucsc.hse.core.comm;

import edu.ucsc.cross.hse.core.object.HybridSystem;
import java.util.List;

public interface Node
{

	public HybridSystem<?> getConnectedSystem();

	public Object getAddress();

	public List<Packet<?>> getAdknowledgedPackets();

	public List<Packet<?>> getPacketSendQueue();

	public List<Packet<?>> getSentPackets();

	public List<Packet<?>> getReceivedPackets();

	/*
	 * Addresses of directly linked nodes that packets can be sent to
	 */
	public List<Object> getLinkedDestinationAddresses();

	/*
	 * Addresses of directly linked nodes that packets can be received from
	 */
	public List<Object> getLinkedSourceAddresses();

	/*
	 * Addresses of directly linked nodes that packets can be sent to
	 */
	public List<Object> getReachableDestinationAddresses();

	/*
	 * Addresses of directly linked nodes that packets can be received from
	 */
	public List<Object> getReachableSourceAddresses();

	/*
	 * Addresses of directly linked nodes that packets can be sent to
	 */
	public List<Link> getPathToDestination(Object destination_address);

	/*
	 * Addresses of directly linked nodes that packets can be sent to
	 */
	public List<Link> getPathFromSource(Object source_address);

	public <P> boolean sendPacket(Packet<P> packet);

}
