package edu.cross.ucsc.hse.core.comm;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

public class NetworkConfiguration extends DirectedMultigraph<Node, Link>
{

	public NetworkConfiguration(EdgeFactory<Node, Link> ef)
	{
		super(ef);
		// TODO Auto-generated constructor stub
	}

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
