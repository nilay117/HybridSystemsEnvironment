package edu.ucsc.cross.hse.core.model;

import edu.ucsc.cross.hse.core.object.HybridSystem;
import org.jgrapht.EdgeFactory;

public interface NetworkFactory<N extends Node, L extends Link> extends EdgeFactory<N, L>
{

	public N createNode(HybridSystem<?> system);

}
