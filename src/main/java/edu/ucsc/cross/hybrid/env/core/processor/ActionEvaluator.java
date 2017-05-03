package edu.ucsc.cross.hybrid.env.core.processor;

import org.apache.commons.math3.ode.events.EventHandler;

import bs.commons.io.system.IO;

class ActionEvaluator extends ProcessorComponent implements EventHandler
{

	public Double flag;

	ActionEvaluator(Environment processor)
	{
		super(processor);
	}

	@Override
	public double g(double t, double[] y)
	{
		getEnvironment().time().seconds(t);
		getComputationEngine().updateValues(y);

		if (getComponents().jumpOccurring())
		{
			flag = -1 * flag;

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
		IO.out(getConsole().getDiscreteEventIndication());
		flag = -1.0 * flag;
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
		getEnvironment().time().seconds(t);
		getData().storeData(t - getSettings().getData().dataStoreIncrement,
		(true && getSettings().getData().storeAtEveryJump));
		while (getComponents().jumpOccurring())
		{
			getComponents().performTasks(true);
		}
		getData().storeData(t, (true && getSettings().getData().storeAtEveryJump));
		getComputationEngine().setODEValueVector(y);
		y = getComputationEngine().getODEValueVector();
		//	System.out.println(y[0] + " " + y[1] + " " + y[2] + " " + y[3]);
	}

}