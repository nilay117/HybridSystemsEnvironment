package edu.cross.ucsc.hse.core.nnn;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DirectedPseudograph;

public class NetworkStructure<V, L>
{

	private DirectedPseudograph<V, L> networkStructure;

	private AllDirectedPaths<V, L> networkPaths;

	public DirectedPseudograph<V, L> getStructure()
	{
		return networkStructure;
	}

	public List<GraphPath<V, L>> getAllPaths(V source, V destination)
	{
		return networkPaths.getAllPaths(source, destination, false, null);
	}

}
