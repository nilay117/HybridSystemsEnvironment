package edu.ucsc.cross.hse.core.n;

import edu.ucsc.cross.hse.core.netG.NetworkNode;
import java.util.HashMap;
import org.jgrapht.graph.DirectedMultigraph;

public class Network
{

	private DirectedMultigraph<Object, ObjectLink> topologyGraph;
	private HashMap<Object, NetworkNode> addressMapping;

}
