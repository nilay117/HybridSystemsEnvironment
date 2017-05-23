package edu.ucsc.cross.hybrid.env.core.processing;

import org.apache.commons.math3.ode.events.EventHandler;

import bs.commons.io.system.IO;

class JumpDetector extends Processor implements EventHandler
{

	public Integer toggles = 0;
	public Double flag;

	JumpDetector(Environment processor)
	{
		super(processor);
	}

	@Override
	public double g(double t, double[] y)
	{
		getEnvironment().getEnvironmentTime().setTime(t);

		getComputationEngine().updateValues(y);
		if (getEnvironment().jumpOccurring())
		{
			flag = -1 * flag;
			toggles = toggles + 1;

		} else
		{
			getData().storeData(t, false);
		}
		return flag;
	}

	@Override
	public EventHandler.Action eventOccurred(double t, double[] y, boolean increasing)
	{
		getComputationEngine().updateValues(y);
		getEnvironment().getEnvironmentTime().setTime(t);
		IO.out(getConsole().getDiscreteEventIndication());
		if (Math.floorMod(toggles, 2) == 0)// && toggles > 1)
		{
			flag = -1.0 * flag;
		}
		toggles = 0;
		return EventHandler.Action.RESET_STATE;
	}

	@Override
	public void init(double t0, double[] y0, double t)
	{

		flag = 1.0;
	}

	@Override
	public void resetState(double t, double[] y)
	{
		getComputationEngine().updateValues(y);
		getEnvironment().getEnvironmentTime().setTime(t);
		// getData().storeData(t - getSettings().getData().dataStoreIncrement,
		// (true && getSettings().getData().storeAtEveryJump));
		getComponents().performTasks(true);
		// getData().storeData(t, (true &&
		// getSettings().getData().storeAtEveryJump));
		getComputationEngine().setODEValueVector(y);
		y = getComputationEngine().getODEValueVector();

	}

}