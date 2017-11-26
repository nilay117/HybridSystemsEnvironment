package edu.cross.ucsc.hse.core.comm;

import java.util.List;

public interface Node
{

	public Object getAddress();

	public List<Pack> getAdknowledgedPackets();

	public List<Pack> getSentPackets();

	public List<Pack> getReceivedPackets();

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

	/*
	 * Addresses of directly linked nodes that packets can be sent to
	 */
	public boolean transmitPacket(Pack packet);

	public Node createNewNode();

}
