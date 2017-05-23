package edu.ucsc.cross.hybrid.env.core.test.components;

import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hybrid.env.core.elements.Behavior;

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
		if (state.hybrid.get() >= 10.0)
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
		state.continuous.setDt(1.0);
		state.hybrid.setDt(state.hybrid.get());
	}

	@Override
	public void jumpMap()
	{
		state.hybrid.set(1.0);
	}

	@Override
	public void initialize()
	{

	}

}