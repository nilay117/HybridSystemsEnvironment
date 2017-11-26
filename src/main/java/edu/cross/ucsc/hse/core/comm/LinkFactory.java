package edu.cross.ucsc.hse.core.comm;

import edu.ucsc.cross.hse.core.object.HybridSystem;
import org.jgrapht.EdgeFactory;

public interface LinkFactory<N extends Node, L extends Link> extends EdgeFactory<N, L>
{

	public N createNode(HybridSystem<?> system);

}
