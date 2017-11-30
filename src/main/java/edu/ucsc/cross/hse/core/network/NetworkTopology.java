package edu.ucsc.cross.hse.core.network;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.collections.set.ListOrderedSet;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DirectedPseudograph;

public class NetworkTopology<N>
{

	public DirectedPseudograph<NetworkNode<N>, NetworkLink<N>> network;
	private AllDirectedPaths<NetworkNode<N>, NetworkLink<N>> paths;
	private HashMap<NetworkNode<N>, HashMap<NetworkNode<N>, ArrayList<GraphPath<NetworkNode<N>, NetworkLink<N>>>>> flowz;
	private HashMap<NetworkLink<N>, HashMap<LinkProperty, Double>> properties;

	public NetworkTopology()
	{
		network = new DirectedPseudograph<NetworkNode<N>, NetworkLink<N>>(new LinkFactory<N>());// f);
		paths = new AllDirectedPaths<NetworkNode<N>, NetworkLink<N>>(network);
	}

	public void addLinkProperty(NetworkLink<N> link, LinkProperty property, Double value)
	{
		if (!properties.containsKey(link))
		{
			properties.put(link, new HashMap<LinkProperty, Double>());
		}
		properties.get(link).put(property, value);
	}

	public HashMap<NetworkNode<N>, HashMap<NetworkNode<N>, ArrayList<GraphPath<NetworkNode<N>, NetworkLink<N>>>>> getAllFlows(
	boolean update)
	{

		HashMap<NetworkNode<N>, HashMap<NetworkNode<N>, ArrayList<GraphPath<NetworkNode<N>, NetworkLink<N>>>>> flows = new HashMap<NetworkNode<N>, HashMap<NetworkNode<N>, ArrayList<GraphPath<NetworkNode<N>, NetworkLink<N>>>>>();
		for (NetworkNode<N> node : network.vertexSet())
		{
			if (!flows.containsKey(node))
			{
				flows.put(node, new HashMap<NetworkNode<N>, ArrayList<GraphPath<NetworkNode<N>, NetworkLink<N>>>>());
			}
			for (NetworkNode<N> dest : network.vertexSet())// network.edgesOf(node))
			{
				// for (Graph<NetworkNode<N>, V> flow : paths.getAllPaths(network, node, dest, false, null))
				{
					try
					{
						ListOrderedSet s = new ListOrderedSet();
						s.add(node);
						ListOrderedSet sd = new ListOrderedSet();
						sd.add(dest);
						if (!flows.get(node).containsKey(dest))
						{
							flows.get(node).put(dest,

							new ArrayList<GraphPath<NetworkNode<N>, NetworkLink<N>>>());// paths.getAllPaths(s,
																						// network.vertexSet(), false,
							// 15)));// paths.getAllPaths(node,
						}
						{
							flows.get(node).get(dest).addAll(paths.getAllPaths(s, sd, true, 25));// dest.getDestination(),
						}
						// false, null);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		this.flowz = flows;
		return flows;
	}

}
