package edu.cross.ucsc.hse.core.comm;

import org.jgrapht.graph.DirectedWeightedMultigraph;

public class BasicNetwork extends DirectedWeightedMultigraph<Node, Link>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7348378569890527624L;

	public BasicNetwork()
	{
		super(new BasicConnectionFactory());
		// TODO Auto-generated constructor stub
	}

}
