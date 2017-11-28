package edu.ucsc.cross.hse.core.network;

public interface Link<V extends NetworkNode<N>, N>
{

	public V getOrigin();

	public V getDestination();

}
