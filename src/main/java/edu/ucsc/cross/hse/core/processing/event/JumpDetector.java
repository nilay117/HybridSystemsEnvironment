package edu.ucsc.cross.hse.core.processing.event;

import org.apache.commons.math3.ode.events.EventHandler;

import edu.ucsc.cross.hse.core.processing.management.Environment;
import edu.ucsc.cross.hse.core.processing.management.ProcessorAccess;

class JumpDetector extends ProcessorAccess implements EventHandler
{

	public Integer toggles = 0; // toggle index to eliminate the error within
								// the apache event handler that sometimes
								// crashes if the value is the same as before
								// the jump
	public Double flag; // flag indicating a jump is pending

	/*
	 * constructor to link the environment
	 */
	JumpDetector(Environment processor)
	{
		super(processor);
	}

	/*
	 * The Threshold checking method that will trigger an interrupt
	 */
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

	/*
	 * Response that occurs when event is detecteds
	 */
	@Override
	public EventHandler.Action eventOccurred(double t, double[] y, boolean increasing)
	{
		getComputationEngine().updateValues(y);
		getEnvironment().getEnvironmentTime().setTime(t);
		this.getConsole().print(getConsole().getDiscreteEventIndication());
		if (Math.floorMod(toggles, 2) == 0)// && toggles > 1)
		{
			flag = -1.0 * flag;
		}
		toggles = 0;
		return EventHandler.Action.RESET_STATE;
	}

	/*
	 * Performs actions necessary to reset the state after a jump has occurred
	 */
	@Override
	public void resetState(double t, double[] y)
	{
		getComputationEngine().updateValues(y);
		getEnvironment().getEnvironmentTime().setTime(t);
		getComponents().performAllTasks(true);
		getComputationEngine().setODEValueVector(y);
		y = getComputationEngine().getODEValueVector();

	}

	/*
	 * initializes the event handler
	 */
	@Override
	public void init(double t0, double[] y0, double t)
	{

		flag = 1.0;
	}
}