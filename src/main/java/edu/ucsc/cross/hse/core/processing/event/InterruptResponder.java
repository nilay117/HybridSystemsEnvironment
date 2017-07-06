package edu.ucsc.cross.hse.core.processing.event;

import org.apache.commons.math3.ode.events.EventHandler;

import edu.ucsc.cross.hse.core.processing.execution.HybridEnvironment;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.models.HybridSystem;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessingConnector;

/*
 * This class is used to terminate the environment early if need be.
 */
public class InterruptResponder extends ProcessingConnector implements EventHandler
{

	public boolean isOutsideDomainError()
	{
		return errorTermination;
	}

	public boolean isTerminating()
	{
		return killFlag;
	}

	public boolean isTerminatedEarly()
	{
		return envStopped;
	}

	private boolean killFlag; // flag that indactes it is wile to kill the
								// process

	private boolean errorTermination;

	private boolean envPaused;

	private boolean envStopped;

	public boolean isPauseTemporary()
	{
		return envPaused;
	}

	/*
	 * Constructor that uses the environment
	 */
	public InterruptResponder(CentralProcessor processor)
	{
		super(processor);

	}

	/*
	 * Initialization if values were to be used to trigger events
	 */
	@Override
	public void init(double t0, double[] y0, double t)
	{
		killFlag = false;
		envPaused = false;
		envStopped = false;
		errorTermination = false;
	}

	/*
	 * The Threshold checking method that will trigger an interrupt
	 */
	@Override
	public double g(double t, double[] y)
	{

		if (killFlag || this.getComponentOperator(getEnv()).outOfAllDomains())
		{
			if (this.getComponentOperator(getEnv()).outOfAllDomains())
			{
				killEnv();
			}
			return -1;
		} else
		{
			return 1;
		}
	}

	/*
	 * response to process being terminated
	 */
	@Override
	public Action eventOccurred(double t, double[] y, boolean increasing)
	{
		if (killFlag)// || this.getComponents().outOfAllDomains())
		{
			return EventHandler.Action.STOP;
		} else
		{
			return EventHandler.Action.CONTINUE;
		}
	}

	/*
	 * State reset
	 */
	@Override
	public void resetState(double t, double[] y)
	{
	}

	/*
	 * Terminates the simulation
	 */
	public void interruptEnv()
	{
		killFlag = true;
	}

	/*
	 * Terminates the simulation
	 */
	public void interruptEnv(boolean terminated_by_error)
	{
		errorTermination = terminated_by_error;
		interruptEnv();
	}

	public void killEnv()
	{
		envStopped = true;
		interruptEnv();
	}

	public void pauseSim()
	{
		envPaused = true;
		interruptEnv();
	}

	public void resumeSim()
	{
		envPaused = false;
		killFlag = false;
	}
}
