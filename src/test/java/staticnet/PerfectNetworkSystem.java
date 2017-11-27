package staticnet;

import edu.ucsc.cross.hse.core.model.Packet;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import edu.ucsc.cross.hse.core.object.Network;
import java.util.HashMap;

public class PerfectNetworkSystem extends HybridSystem<PerfectNetworkState>
{

	private Network<GeneralNode, GeneralLink> network;
	private HashMap<Object, GeneralNode> addresses;

	public PerfectNetworkSystem(PerfectNetworkState state, GeneralNetwork net)
	{
		super(state);
		network = net;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean C(PerfectNetworkState x)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void F(PerfectNetworkState x, PerfectNetworkState x_dot)
	{
		// TODO Auto-generated method stub

	}

	public GeneralNode findAddress(Object obj)
	{
		if (!addresses.containsKey(obj))
		{
			for (GeneralNode node : network.getNetwork().vertexSet())
			{
				if (!addresses.containsKey(node.getAddress()))
				{
					addresses.put(node.getAddress(), node);
				}
			}
		}
		return addresses.get(obj);
	}

	@Override
	public boolean D(PerfectNetworkState x)
	{
		for (GeneralNode node : network.getNetwork().vertexSet())
		{
			if (node.getPacketQueue().size() > 0)
			{
				for (Packet<?> packet : node.getPacketQueue())
				{
					if (network.getNetwork().containsEdge(findAddress(packet.getHeader().getSourceAddress()),
					findAddress(packet.getHeader().getDestinationAddress())))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void G(PerfectNetworkState x, PerfectNetworkState x_plus)
	{
		for (GeneralNode node : network.getNetwork().vertexSet())
		{
			if (node.getPacketQueue().size() > 0)
			{
				for (Packet<?> packet : node.getPacketQueue())
				{
					if (network.getNetwork().containsEdge(findAddress(packet.getHeader().getSourceAddress()),
					findAddress(packet.getHeader().getDestinationAddress())))
					{
						node.getSentPackets().add(packet);
						node.getPacketQueue().remove(packet);
						findAddress(packet.getHeader().getDestinationAddress()).getReceivedPackets().add(packet);
					}
				}
			}
		}
	}

}
