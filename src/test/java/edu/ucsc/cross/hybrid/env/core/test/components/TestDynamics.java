package edu.ucsc.cross.hybrid.env.core.test.components;

import edu.ucsc.cross.hse.core.component.constructors.Behavior;

public class TestDynamics extends Behavior
{

	public TestState state; // bouncing ball state

	/*
	 * Constructor and state loader
	 */

	public TestDynamics(TestState state)
	{
		super("Test Dynamics");
		this.state = state;
	}

	// Trigger a jump when the hybrid variable reaches 10
	@Override
	public boolean jumpSet()
	{
		boolean jumpOccurring = false;
		if (state.hybrid.getValue().kilometersPerSecond() >= 10.0)
		{
			jumpOccurring = true;
		}
		return jumpOccurring;
	}

	@Override
	public boolean flowSet()
	{
		return true;
	}

	@Override
	public void flowMap()
	{
		state.continuous.setDerivative(1.0);
		state.hybrid.setDerivative(state.hybrid.getValue());
	}

	@Override
	public void jumpMap()
	{
		state.hybrid.getValue().kilometersPerSecond(Math.random() * 3 + Math.random() + 1);
	}

	@Override
	public void initialize()
	{

	}

}