package edu.ucsc.cross.hse.core.processing.event;

import org.apache.commons.math3.ode.events.EventHandler;

import edu.ucsc.cross.hse.core.framework.component.ComponentWorker;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

public class JumpEvaluator extends ProcessorAccess implements EventHandler
{

	public Integer toggles = 0; // toggle index to eliminate the error within
								// the apache event handler that sometimes
								// crashes if the value is the same as before
								// the jump
	public Double flag; // flag indicating a jump is pending

	/*
	 * constructor to link the environment
	 */
	public JumpEvaluator(CentralProcessor processor)
	{
		super(processor);
	}

	/*
	 * The Threshold checking method that will trigger an interrupt
	 */
	@Override
	public double g(double t, double[] y)
	{
		getComputationEngine().updateValues(y);
		if (this.getComponentOperator(getEnv()).isJumpOccurring())
		{

			flag = -1 * flag;
			toggles = toggles + 1;

		} else
		{
			getEnvironmentOperator().getEnvironmentHybridTime().setTime(t);
			if (ComponentWorker.getOperator(getEnv()).outOfAllDomains())
			{
				getData().storeData(t, true);
			} else
			{
				getData().storeData(t, false);
			}
		}
		return flag;
	}

	/*
	 * Response that occurs when event is detecteds
	 */
	@Override
	public EventHandler.Action eventOccurred(double t, double[] y, boolean increasing)
	{

		this.getConsole().print(getConsole().getDiscreteEventIndication()); // print
																			// notification
																			// if
																			// enabled
		if (Math.floorMod(toggles, 2) == 0) // check to see if an odd number of
											// switches occurred
		{
			flag = -1.0 * flag; // adjust the flag if so to avoid integrator
								// erors
		}
		toggles = 0; // reset the toggle counter

		if (getEnv().getJumpIndex() < getSettings().getExecutionSettings().jumpLimit)
		{
			// getComputationEngine().updateValues(y); // load new ode values

			// getEnvironmentOperator().getEnvironmentHybridTime().setTime(t -
			// Double.MIN_VALUE);
			// getEnvironmentOperator().getEnvironmentHybridTime().setTime(t +
			// Double.MIN_VALUE);

			// this.getData().storeData(getEnv().getEnvironmentTime(), true);
			// getEnvironmentOperator().getEnvironmentHybridTime().setTime(t);
			return EventHandler.Action.RESET_STATE; // continue if jump limit
													// hasn't been reached
		} else
		{
			return EventHandler.Action.STOP; // otherwise terminate the
												// environment
		}
	}

	/*
	 * Performs actions necessary to reset the state after a jump has occurred
	 */

	@Override
	public void resetState(double t, double[] y)
	{
		getComputationEngine().updateValues(y); // load new ode values

		getEnvironmentOperator().getEnvironmentHybridTime().incrementJumpIndex(0); // refresh

		getData().storeData(getEnvironmentOperator().getEnvironmentHybridTime().getTime(), true);

		getEnvironmentOperator().getEnvironmentHybridTime().setTime(t); // store
																		// most
																		// recent
																		// time

		getComponents().performAllTasks(true); // execute all jumps

		getComputationEngine().setODEValueVector(y); // update the ode vector

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