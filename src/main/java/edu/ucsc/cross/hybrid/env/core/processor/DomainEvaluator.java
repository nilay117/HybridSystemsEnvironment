package edu.ucsc.cross.hybrid.env.core.processor;

import org.apache.commons.math3.ode.events.EventHandler;

import bs.commons.io.system.IO;

class DomainEvaluator extends Processor implements EventHandler
{

	public Double flag = 1.0;

	DomainEvaluator(Environment processor)
	{
		super(processor);

	}

	public double g(double t, double[] y)
	{
		//System.out.println(XMLParser.serializeObject(y, MessageCategory.DEV));
		//getEnvironment().getSimTime().seconds(t);
		getComputationEngine().updateValues(y);

		if (getComponents().jumpOccurring())
		{
			//getEnvironment().storePreJumpStates();
			//getSimEngine().updateValues(y);
			return -1 * flag;

		} else

		{
			getData().storeData(t, false);
			return 1 * flag;
		}
	}

	public EventHandler.Action eventOccurred(double t, double[] y, boolean increasing)
	{
		//getComputationEngine().updateValues(y);
		IO.out(getConsole().getDiscreteEventIndication());
		flag = -1.0 * flag;
		return EventHandler.Action.RESET_STATE;
	}

	@Override
	public void init(double t0, double[] y0, double t)
	{
	}

	@Override
	public void resetState(double t, double[] y)
	{
		getComputationEngine().updateValues(y);
		getEnvironment().getEnvTime().seconds(t);
		getComponents().performTasks(true);
		getData().storeData(t, (true && getSettings().getData().storeAtEveryJump));
		getComputationEngine().setODEValueVector(y);
		y = getComputationEngine().getODEValueVector();
		//	System.out.println(y[0] + " " + y[1] + " " + y[2] + " " + y[3]);
	}

}