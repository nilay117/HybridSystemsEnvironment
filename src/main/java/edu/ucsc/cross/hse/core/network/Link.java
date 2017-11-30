package edu.ucsc.cross.hse.core.network;

public interface Link<N>
{

	public NetworkNode<N> getOrigin();

	public NetworkNode<N> getDestination();

}
