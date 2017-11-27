package edu.cross.ucsc.hse.core.model.perfectnetwork;

public interface Link<V extends NetworkNode<N>, N>
{

	public V getOrigin();

	public V getDestination();

}
