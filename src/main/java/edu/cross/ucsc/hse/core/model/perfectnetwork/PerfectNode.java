package edu.cross.ucsc.hse.core.model.perfectnetwork;

import edu.ucsc.cross.hse.core.network.Node;
import edu.ucsc.cross.hse.core.network.Packet;
import java.util.ArrayList;
import java.util.List;

public class PerfectNode implements Node
{

	@Override
	public Object getAddress()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getReachableAddresses()
	{
		ArrayList<Object> reachableAddresses = null;
		return reachableAddresses;
	}

	@Override
	public ArrayList<Object> getConnectedAddresses()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Packet> getTransmissionQueue()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Packet> getSuccessfulPackets()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Packet> getFailedPackets()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
