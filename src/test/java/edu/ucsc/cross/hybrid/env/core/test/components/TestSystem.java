package edu.ucsc.cross.hybrid.env.core.test.components;

import edu.ucsc.cross.hse.core.framework.component.Component;

public class TestSystem extends Component// HybridSystem
{

	public TestState stsate;
	public TestDynamics testdynamics;

	public TestSystem()
	{
		super("Test System");
		stsate = new TestState();
		testdynamics = new TestDynamics(stsate);
	}

	public TestSystem(String name)
	{
		super(name);
		stsate = new TestState();
		testdynamics = new TestDynamics(stsate);
	}

	@Override
	public void initialize()
	{
		// TODO Auto-generated method stub

	}
}
