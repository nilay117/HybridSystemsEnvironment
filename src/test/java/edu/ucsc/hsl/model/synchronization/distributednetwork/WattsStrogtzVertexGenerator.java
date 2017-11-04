package edu.ucsc.hsl.model.synchronization.distributednetwork;

import org.jgrapht.VertexFactory;

import Jama.Matrix;

public class WattsStrogtzVertexGenerator implements VertexFactory<Integer>
{

	Integer vertex = 0;

	public WattsStrogtzVertexGenerator()
	{

	}

	@Override
	public Integer createVertex()
	{
		// TODO Auto-generated method stub
		return vertex++;
	}

}
