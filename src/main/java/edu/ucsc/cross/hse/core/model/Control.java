package edu.ucsc.cross.hse.core.model;

public interface Control<X, U>
{

	public U u(X x);
}
