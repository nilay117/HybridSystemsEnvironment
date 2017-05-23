package edu.ucsc.cross.hybrid.env.core.test.components;

import edu.ucsc.cross.hybrid.env.core.components.HybridSystem;

public class TestSystem extends HybridSystem
{

	public final TestState state;
	public final TestDynamics dynamics;

	public TestSystem()
	{
		super("Test System");
		state = new TestState();
		dynamics = new TestDynamics(state);
	}

	@Override
	public void initialize()
	{
		// TODO Auto-generated method stub

	}
}
