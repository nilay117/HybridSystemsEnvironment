package edu.ucsc.cross.hse.core.graph;

import org.jgrapht.EdgeFactory;
import org.jgrapht.VertexFactory;
import org.jgrapht.generate.WattsStrogatzGraphGenerator;

public class NetworkGraphFactory implements VertexFactory<Integer>, EdgeFactory<Integer, NetworkEdge>
{

	@Override
	public NetworkEdge createEdge(Integer arg0, Integer arg1)
	{
		// TODO Auto-generated method stub
		return new NetworkEdge(arg0, arg1);
	}

	Integer vertex = 0;

	public NetworkGraphFactory()
	{

	}

	@Override
	public Integer createVertex()
	{
		return vertex++;
	}

	public static NetworkGraph generateWattsStrogatzGraph(Integer n, Integer k, double p)
	{
		WattsStrogatzGraphGenerator<Integer, NetworkEdge> wsGen = new WattsStrogatzGraphGenerator<Integer, NetworkEdge>(
		n, k, p);
		NetworkGraphFactory gen = new NetworkGraphFactory();
		NetworkGraph graph = new NetworkGraph();
		wsGen.generateGraph(graph, gen, null);
		return graph;
	}
}