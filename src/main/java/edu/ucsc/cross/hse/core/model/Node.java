package edu.ucsc.cross.hse.core.model;

import com.be3short.obj.organization.Address;
import edu.ucsc.cross.hse.core.n.ObjectLink;
import java.util.List;
import java.util.Set;
import org.jgrapht.GraphPath;

public interface Node
{

	public Object getAddress();

	public List<Packet<?>> getSentPackets();

	public List<Packet<?>> getReceivedPackets();

	public Set<Connection> getIncomingConnections();

	public Set<Connection> getOutgoingConnections();

	public GraphPath<Address, ObjectLink> getPath(Object source, Object dest);

	/*
	 * Addresses of directly linked nodes that packets can be sent to
	 */
	public Set<Object> getLinkedDestinationAddresses();

	/*
	 * Addresses of directly linked nodes that packets can be received from
	 */
	public Set<Link> getLinkedSourceAddresses();

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
	public List<Object> getPathToDestination(Object destination_address);

	/*
	 * Addresses of directly linked nodes that packets can be sent to
	 */
	public List<Object> getPathFromSource(Object source_address);

	public <P> boolean sendPacket(Packet<P> packet);

}
