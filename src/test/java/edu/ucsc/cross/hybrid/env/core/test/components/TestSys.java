package edu.ucsc.cross.hybrid.env.core.test.components;

public class TestSys extends TestDynamics
{

	public TestSys(TestState state)
	{
		super(state);
		// TODO Auto-generated constructor stub
	}

	// Trigger a jump when the hybrid variable reaches 10
	@Override
	public boolean jumpSet()
	{
		boolean jumpOccurring = false;
		if (state.hybrid.get() >= 30.0)
		{
			jumpOccurring = true;
		}
		return jumpOccurring;
	}

	@Override
	public void jumpMap()
	{
		state.hybrid.set(Math.random() * 20 + Math.random() + 1);
	}
}
