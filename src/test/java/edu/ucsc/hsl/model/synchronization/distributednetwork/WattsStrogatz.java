package edu.ucsc.hsl.model.synchronization.distributednetwork;

import Jama.Matrix;
import org.jgrapht.Graph;
import org.jgrapht.generate.WattsStrogatzGraphGenerator;
import org.jgrapht.graph.Pseudograph;

public class WattsStrogatz
{

	public static Graph<Integer, Edge> generateWattsStrogatzGraph(Integer n, Integer k, double p)
	{
		WattsStrogatzGraphGenerator<Integer, Edge> wsGen = new WattsStrogatzGraphGenerator<Integer, Edge>(n, k, p);

		WattsStrogtzVertexGenerator gen = new WattsStrogtzVertexGenerator();
		WSEdgeFactory eg = new WSEdgeFactory();
		Pseudograph<Integer, Edge> graph = new Pseudograph<Integer, Edge>(eg);
		wsGen.generateGraph(graph, gen, null);
		return graph;

		// Graph<V, Integer> graph = wsGen.generateGraph(arg0, arg1, arg2);
	}

	public static Matrix getMatrix(Integer n, Integer k, double p)
	{
		Graph<Integer, Edge> g = generateWattsStrogatzGraph(n, k, p);
		double[][] matrixArray = new double[n][n];
		for (Edge e : g.edgeSet())
		{
			matrixArray[e.a][e.b] = 1;
			System.out.println(e.a + " , " + e.b);
		}
		Matrix matrix = new Matrix(matrixArray);
		matrix.print(10, 0);
		return matrix;
	}

	public static void main(String args[])
	{
		Matrix mat = getMatrix(25, 4, .1);
	}

	public static void testGraphGettermain(String args[])
	{
		Graph<Integer, Edge> g = generateWattsStrogatzGraph(100, 4, .1);

		for (Edge e : g.edgeSet())
		{
			System.out.println(e.a + " , " + e.b);
		}
		System.out.println(g.edgeSet().size());
		Matrix m = new Matrix(1, 2);

	}

}