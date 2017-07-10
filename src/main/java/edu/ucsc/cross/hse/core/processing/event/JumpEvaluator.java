package edu.ucsc.cross.hse.core.processing.event;

import org.apache.commons.math3.ode.events.EventHandler;

import edu.ucsc.cross.hse.core.framework.component.FullComponentOperator;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
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
			if (FullComponentOperator.getOperator(getEnv()).outOfAllDomains())
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
		HybridTime preJumpTime = getEnvironmentOperator().getEnvironmentHybridTime().getCurrent();
		getEnvironmentOperator().getEnvironmentHybridTime().incrementJumpIndex(0);
		// HybridTime preJumpTime =
		// getEnvironmentOperator().getEnvironmentHybridTime().getCurrent();
		// HybridTime preJumpTime =
		// getEnvironmentOperator().getEnvironmentHybridTime().getCurrent();
		getEnvironmentOperator().getEnvironmentHybridTime().setTime(t); // store
																		// new
																		// step
																		// time
		// HybridTime preJumpTime =
		// getEnvironmentOperator().getEnvironmentHybridTime().getCurrent();
		// HybridTime preJumpTime =
		// getEnvironmentOperator().getEnvironmentHybridTime().getCurrent();
		getEnvironmentOperator().getEnvironmentHybridTime().incrementJumpIndex();
		// HybridTime preJumpTime =
		// getEnvironmentOperator().getEnvironmentHybridTime().getCurrent();

		while (getEnvironmentOperator().isJumpOccurring())
		{

			getComponents().performAllTasks(true); // execute all jumps
			getData().storeJumpData(preJumpTime);
			this.getComponents().clearPreJumpData();
			if (getEnvironmentOperator().isJumpOccurring())
			{
				getEnvironmentOperator().getEnvironmentHybridTime().incrementJumpIndex();
				preJumpTime = new HybridTime(preJumpTime.getTime(), preJumpTime.getJumpIndex() + 1);// getEnvironmentOperator().getEnvironmentHybridTime().getCurrent();

				//
				getEnvironmentOperator().getEnvironmentHybridTime().incrementJumpIndex();
				getEnvironmentOperator().getEnvironmentHybridTime()
				.setTime(getEnvironmentOperator().getEnvironmentHybridTime().getTime() + Double.MIN_VALUE);

			}
		}
		// getEnvironmentOperator().getEnvironmentHybridTime().incrementJumpIndex();
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