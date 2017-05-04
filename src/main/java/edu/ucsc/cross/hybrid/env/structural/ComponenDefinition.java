package edu.ucsc.cross.hybrid.env.structural;

public interface ComponenDefinition
{

	public <T> Class<T> baseComponentClass();

	public String componentLabel();
}
