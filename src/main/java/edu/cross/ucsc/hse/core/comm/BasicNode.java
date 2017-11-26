package edu.cross.ucsc.hse.core.comm;

import edu.ucsc.cross.hse.core.object.ObjectSet;
import java.util.List;

public class BasicNode extends ObjectSet implements Node
{

	@Override
	public Object getAddress()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Pack> getAdknowledgedPackets()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Pack> getSentPackets()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Pack> getReceivedPackets()
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
	public boolean transmitPacket(Pack packet)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Node createNewNode()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
