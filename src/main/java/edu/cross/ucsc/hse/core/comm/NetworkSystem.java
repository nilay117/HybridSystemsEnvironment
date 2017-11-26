package edu.cross.ucsc.hse.core.comm;

import edu.ucsc.cross.hse.core.object.HybridSystem;
import edu.ucsc.cross.hse.core.object.ObjectSet;
import java.util.HashMap;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedWeightedMultigraph;

public abstract class NetworkSystem<S extends ObjectSet> extends HybridSystem<S>
{

	DirectedWeightedMultigraph<Node, Link> topology;

	HashMap<Node, HybridSystem<?>> agents;

	public NetworkSystem(DirectedWeightedMultigraph<Node, Link> topology, S state)
	{
		super(state);
		this.topology = topology;
		// TODO Auto-generated constructor stub
	}

	public Node connectSystem(HybridSystem<?> system)
	{
		if (!agents.containsKey(system))
		{

		}
		Node systemNode = agents.get(system);
		agents.put(node, system);
	}

	public Graph<N, L> getConnectivityGraph();

}
