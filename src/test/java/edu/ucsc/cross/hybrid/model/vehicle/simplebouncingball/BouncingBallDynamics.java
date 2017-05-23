package edu.ucsc.cross.hybrid.model.vehicle.simplebouncingball;

import edu.ucsc.cross.hse.core.component.constructors.Behavior;

public class BouncingBallDynamics extends Behavior
{

	public BouncingBallState state; // bouncing ball state

	/*
	 * Constructor and state loader
	 */
	public BouncingBallDynamics(BouncingBallState ball)
	{
		super("Simple Bouncing Ball Dynamical Model");
		state = ball;
	}

	@Override
	public boolean jumpSet()
	{
		boolean impactOccurring = false;
		if (state.yPos.get().meters() <= 0 && state.yVel.get().metersPerSecond() < 0.0)
		{
			impactOccurring = true;
		}
		return impactOccurring;
	}

	@Override
	public boolean flowSet()
	{
		return true;
	}

	@Override
	public void flowMap()
	{
		state.yVel.getDt().metersPerSecond(-9.81);
		state.xPos.getDt().meters(state.xVel.get().metersPerSecond());
		state.yPos.getDt().meters(state.yVel.get().metersPerSecond());
	}

	@Override
	public void jumpMap()
	{
		//System.out.println("Bouncing Ball (pre-jump) Jump Map triggered for state " + XMLParser.serializeObject(state));
		state.yVel.get().metersPerSecond(-state.yVel.get().metersPerSecond() * state.envProps.restitution.get());
		//System.out
		//.println("Bouncing Ball (post-jump) Jump Map triggered for state " + XMLParser.serializeObject(state));
	}

	@Override
	public void initialize()
	{
		flowMap();
	}

}
