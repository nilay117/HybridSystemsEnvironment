package edu.ucsc.cross.hybrid.env.core.classifications;

public interface ComponentClass
{

	public <T> Class<T> baseComponentClass();

	public String componentLabel();
}
