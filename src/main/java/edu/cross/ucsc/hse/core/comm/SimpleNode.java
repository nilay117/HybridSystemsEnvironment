package edu.cross.ucsc.hse.core.comm;

import edu.ucsc.cross.hse.core.object.ObjectSet;
import java.util.ArrayList;
import java.util.List;

public class SimpleNode extends ObjectSet implements Node
{

	private String address;
	private NetworkConfiguration network;
	private ArrayList<Packet<?>> pendingPackets;

	@Override
	public Object getAddress()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Packet<?>> getAdknowledgedPackets()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Packet<?>> getSentPackets()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Packet<?>> getReceivedPackets()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getLinkedDestinationAddresses()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getLinkedSourceAddresses()
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
	public List<Link> getPathToDestination(Object destination_address)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Link> getPathFromSource(Object source_address)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <P> boolean transmitPacket(Packet<P> packet)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
