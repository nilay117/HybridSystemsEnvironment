package edu.ucsc.cross.hse.core.network;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.collections.set.ListOrderedSet;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DirectedPseudograph;

public class NetworkTopology<V extends NetworkNode<N>, L extends NetworkLink<V, N>, N>
{

	public NetworkTopology(Class<L> edge_class)
	{
		network = new DirectedPseudograph<V, L>(edge_class);// f);
		paths = new AllDirectedPaths<V, L>(network);
	}

	public DirectedPseudograph<V, L> network;
	private AllDirectedPaths<V, L> paths;
	private HashMap<V, HashMap<V, ArrayList<GraphPath<V, L>>>> flowz;
	private HashMap<L, HashMap<LinkProperty, Double>> properties;

	public void addLinkProperty(L link, LinkProperty property, Double value)
	{
		if (!properties.containsKey(link))
		{
			properties.put(link, new HashMap<LinkProperty, Double>());
		}
		properties.get(link).put(property, value);
	}

	public HashMap<V, HashMap<V, ArrayList<GraphPath<V, L>>>> getAllFlows(boolean update)
	{

		HashMap<V, HashMap<V, ArrayList<GraphPath<V, L>>>> flows = new HashMap<V, HashMap<V, ArrayList<GraphPath<V, L>>>>();
		for (V node : network.vertexSet())
		{
			if (!flows.containsKey(node))
			{
				flows.put(node, new HashMap<V, ArrayList<GraphPath<V, L>>>());
			}
			for (V dest : network.vertexSet())// network.edgesOf(node))
			{
				// for (Graph<V, V> flow : paths.getAllPaths(network, node, dest, false, null))
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

							new ArrayList<GraphPath<V, L>>());// paths.getAllPaths(s, network.vertexSet(), false,
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
