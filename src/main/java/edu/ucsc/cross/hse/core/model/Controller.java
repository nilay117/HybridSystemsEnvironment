package edu.ucsc.cross.hse.core.model;

public interface Controller<X, U>
{

	public U u(X x);
}
