package edu.ucsc.cross.hse.core.obj.structure;

public class State
{

	private String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public State(String name)
	{
		this.name = name;
	}

	public State()
	{
		this.name = this.getClass().getSimpleName();
	}
}
