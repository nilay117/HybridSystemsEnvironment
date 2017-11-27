package edu.cross.ucsc.hse.core.model.perfectnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.collections.set.ListOrderedSet;
import org.jgrapht.EdgeFactory;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DirectedPseudograph;

public class NetworkTopology<V extends NetworkNode<N>, L extends NetworkLink<V, N>, N>
{

	public NetworkTopology(EdgeFactory<V, L> f)
	{
		network = new DirectedPseudograph<V, L>(f);
		paths = new AllDirectedPaths<V, L>(network);
	}

	public DirectedPseudograph<V, L> network;
	private AllDirectedPaths<V, L> paths;
	private HashMap<V, HashMap<V, ArrayList<GraphPath<V, L>>>> flowz;

	// public <V extends NetworkNode<N><NetworkTopology<V, N>()
	// {
	// network = new DirectedPseudograph<V, NetworkLink<V, N>>(new NetworkTopology<V, N> ());
	// }

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

	public static class SNode<M> extends NetworkNode<M>
	{

		String m;

		public SNode(M node_state)
		{
			super(node_state);
			// TODO Auto-generated constructor stub
		}

	}

	public static class SLink<M> extends NetworkLink<SNode<M>, M> implements EdgeFactory<SNode<M>, SLink<M>>
	{

		public SLink(SNode<M> org, SNode<M> dest)
		{
			super(org, dest);
			// TODO Auto-generated constructor stub
		}

		@Override
		public SLink<M> createEdge(SNode<M> sourceVertex, SNode<M> targetVertex)
		{
			// TODO Auto-generated method stub
			return new SLink<M>(sourceVertex, targetVertex);
		}

	}

	public static class M
	{

		public M()
		{

		}
	}

	public static void main(String args[])
	{
		NetworkTopology<SNode<M>, SLink<M>, M> top = new NetworkTopology<SNode<M>, SLink<M>, M>(
		new SLink<M>(null, null));
		SNode<M> one = new SNode<M>(new M());
		SNode<M> two = new SNode<M>(new M());
		SNode<M> three = new SNode<M>(new M());
		SNode<M> four = new SNode<M>(new M());
		SNode<M> five = new SNode<M>(new M());

		top.network.addVertex(one);
		top.network.addVertex(two);
		top.network.addVertex(three);
		top.network.addVertex(four);
		top.network.addVertex(five);
		top.network.addEdge(one, two);
		top.network.addEdge(one, three);
		top.network.addEdge(two, three);
		top.network.addEdge(three, one);
		top.network.addEdge(three, four);
		top.network.addEdge(three, five);

		System.out.println(one + "\n" + two + "\n" + three + "\n" + four);
		// System.out.println(XMLParser.serializeObject(top.getAllFlows(true)));
		System.out.println(top.getAllFlows(true).get(one).get(four).get(1).getLength());// .values());

		System.out.println(top.getAllFlows(true).get(three).get(two));// .values());
	}

}
