package edu.ucsc.cross.hse.core.n;

import org.jgrapht.EdgeFactory;

public class ObjectGraphFactory implements EdgeFactory<Object, ObjectLink>
{

	public ObjectGraphFactory()
	{

	}

	@Override
	public ObjectLink createEdge(Object sourceVertex, Object targetVertex)
	{
		// TODO Auto-generated method stub
		return new ObjectLink(sourceVertex, targetVertex);
	}

}