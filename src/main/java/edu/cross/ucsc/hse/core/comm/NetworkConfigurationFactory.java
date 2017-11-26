package edu.cross.ucsc.hse.core.comm;

public interface NetworkConfigurationFactory<N extends Node, L extends Link>
{

	public L createLink(N source, N destination)

}
