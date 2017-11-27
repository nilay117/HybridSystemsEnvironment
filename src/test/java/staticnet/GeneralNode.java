package staticnet;

import edu.ucsc.cross.hse.core.model.Link;
import edu.ucsc.cross.hse.core.model.Node;
import edu.ucsc.cross.hse.core.model.Packet;
import edu.ucsc.cross.hse.core.object.Network;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GeneralNode implements Node
{

	private Network<Node, Link> network;
	private ArrayList<Packet<?>> packetQueue;
	private ArrayList<Packet<?>> receivedPackets;
	private ArrayList<Packet<?>> sentPackets;
	private String address;

	public GeneralNode()
	{
		address = this.toString();
		packetQueue = new ArrayList<Packet<?>>();
		receivedPackets = new ArrayList<Packet<?>>();
		sentPackets = new ArrayList<Packet<?>>();
	}

	public void linkSystem(Network<Node, Link> network)
	{
		this.network = network;
	}

	@Override
	public Object getAddress()
	{
		if (address == null)
		{
			address = this.toString();
		}
		return address;
	}

	@Override
	public List<Packet<?>> getPacketQueue()
	{
		// TODO Auto-generated method stub
		return packetQueue;
	}

	@Override
	public List<Packet<?>> getSentPackets()
	{
		// TODO Auto-generated method stub
		return sentPackets;
	}

	@Override
	public List<Packet<?>> getReceivedPackets()
	{
		// TODO Auto-generated method stub
		return receivedPackets;
	}

	@Override
	public Set<Link> getLinkedDestinationAddresses()
	{
		// TODO Auto-generated method stub
		return network.getNetwork().edgesOf(this);
	}

	@Override
	public Set<Link> getLinkedSourceAddresses()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getReachableDestinationAddresses()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getReachableSourceAddresses()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getPathToDestination(Object destination_address)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getPathFromSource(Object source_address)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <P> boolean sendPacket(Packet<P> packet)
	{
		if (!packetQueue.contains(packet))
		{
			packetQueue.add(packet);
			return true;
		} else
		{
			return false;
		}
	}

}
