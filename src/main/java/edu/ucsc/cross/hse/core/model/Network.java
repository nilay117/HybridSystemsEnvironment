package edu.ucsc.cross.hse.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.collections.set.ListOrderedSet;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DirectedPseudograph;

public class Network<N, C, P>
{

	private DirectedPseudograph<N, C> graph;
	private AllDirectedPaths<N, C> paths;
	private HashMap<C, P> properties;

	public Network(Class<C> connection_class)
	{
		graph = new DirectedPseudograph<N, C>(connection_class);// f);
		paths = new AllDirectedPaths<N, C>(graph);
	}

	public void setConnectionProperties(C link, P properties)
	{

		this.properties.put(link, properties);
	}

	public HashMap<N, HashMap<N, ArrayList<GraphPath<N, C>>>> getPaths()
	{

		HashMap<N, HashMap<N, ArrayList<GraphPath<N, C>>>> flows = new HashMap<N, HashMap<N, ArrayList<GraphPath<N, C>>>>();
		for (N node : graph.vertexSet())
		{
			if (!flows.containsKey(node))
			{
				flows.put(node, new HashMap<N, ArrayList<GraphPath<N, C>>>());
			}
			for (N dest : graph.vertexSet())// network.edgesOf(node))
			{
				// for (Graph<N, V> flow : paths.getAllPaths(network, node, dest, false, null))
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

							new ArrayList<GraphPath<N, C>>());// paths.getAllPaths(s,
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
		return flows;
	}

	public DirectedPseudograph<N, C> getGraph()
	{
		return graph;
	}

	public HashMap<C, P> getProperties()
	{
		return properties;
	}

}
