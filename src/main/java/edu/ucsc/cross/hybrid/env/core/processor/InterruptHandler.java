package edu.ucsc.cross.hybrid.env.core.processor;

import org.apache.commons.math3.ode.events.EventHandler;

public class InterruptHandler extends ProcessorComponent implements EventHandler
{

	private boolean killFlag;

	InterruptHandler(Environment processor)
	{
		super(processor);
	}

	@Override
	public void init(double t0, double[] y0, double t)
	{
	}

	@Override
	public double g(double t, double[] y)
	{
		if (killFlag)
		{
			return -1;
		} else
		{
			return 1;
		}
	}

	@Override
	public Action eventOccurred(double t, double[] y, boolean increasing)
	{
		if (killFlag)
		{
			return EventHandler.Action.STOP;
		} else
		{
			return EventHandler.Action.CONTINUE;
		}
	}

	@Override
	public void resetState(double t, double[] y)
	{
	}

	public void killSim()
	{
		killFlag = true;
	}
}
