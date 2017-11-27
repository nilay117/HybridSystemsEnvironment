package edu.ucsc.cross.hse.core.object;

import edu.ucsc.cross.hse.core.model.Link;
import edu.ucsc.cross.hse.core.model.NetworkFactory;
import edu.ucsc.cross.hse.core.model.Node;
import java.util.HashMap;
import org.jgrapht.graph.DirectedMultigraph;

public abstract class Network<N extends Node, L extends Link>
{

	protected DirectedMultigraph<N, L> network;

	public DirectedMultigraph<N, L> getNetwork()
	{
		return network;
	}

	public void setNetwork(DirectedMultigraph<N, L> network)
	{
		this.network = network;
	}

	private NetworkFactory<N, L> linkFactory;
	private HashMap<HybridSystem<?>, N> systems;

	public Network(NetworkFactory<N, L> link_factory)
	{
		linkFactory = link_factory;
		systems = new HashMap<HybridSystem<?>, N>();
		network = new DirectedMultigraph<N, L>(link_factory);
	}

	public N createNode()
	{
		return linkFactory.createNode(null);
	}

	public N addSystem(HybridSystem<?> system, N nod)
	{
		N node = nod;
		if (node == null)// && !systems.containsKey(system))
		{
			node = linkFactory.createNode(system);
		}
		try
		{
			// if (!systems.containsKey(system))
			{
				systems.put(system, node);
				network.addVertex(node);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		node = systems.get(system);
		return node;
	}

	public N getSystem(HybridSystem<?> system)
	{
		N node = null;
		if (systems.containsKey(system))
		{
			node = systems.get(system);
		}
		return node;
	}

	public void linkSystems(HybridSystem<?> source, HybridSystem<?> destination)
	{
		N src = addSystem(source, null);
		N dest = addSystem(destination, null);
		boolean containsLink = false;
		for (L link : network.edgeSet())
		{
			if (link.getSender().equals(src) && link.getReceiver().equals(dest))
			{
				containsLink = true;
				break;
			}
		}
		if (!containsLink)
		{
			System.out.println("edge");
			// linkFactory.createEdge(src, dest);
			network.addEdge(src, dest);
		}
	}
}
