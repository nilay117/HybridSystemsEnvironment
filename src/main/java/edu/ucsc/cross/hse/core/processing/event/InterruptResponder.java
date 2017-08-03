package edu.ucsc.cross.hse.core.processing.event;

import org.apache.commons.math3.ode.events.EventHandler;

import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

/*
 * This class is used to terminate the environment early if need be.
 */
public class InterruptResponder extends ProcessorAccess implements EventHandler
{

	/*
	 * Flag if the termination occurd due to an error
	 */
	public boolean isErrorTermination()
	{
		return errorTermination;
	}

	/*
	 * Flag if the execution attempt terminating
	 */
	public boolean isTerminating()
	{
		return killFlag;
	}

	/*
	 * Flag if the execution attempt terminating early
	 */
	public boolean isTerminatedEarly()
	{
		return envStopped;
	}

	/*
	 * Flag that indactes it is wile to kill the process
	 */
	private boolean killFlag;

	/*
	 * Flag indicating that the termination is due to an error
	 */
	private boolean errorTermination;

	/*
	 * Flag indicating that the environment is paused
	 */
	private boolean envPaused;

	/*
	 * Flag indicating that the environment is stopped
	 */
	private boolean envStopped;

	/*
	 * Is the environment paused
	 */
	public boolean isPaused()
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
	 * Response to process being terminated
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
	 * Interrupt the environment
	 */
	public void interruptEnv()
	{
		killFlag = true;
	}

	/*
	 * Interrupt the environment and flag if the interrupt is due to an error
	 */
	public void interruptEnv(boolean terminated_by_error)
	{
		errorTermination = terminated_by_error;
		interruptEnv();
	}

	/*
	 * Terminate the simulation
	 */
	public void killEnv()
	{
		envStopped = true;
		interruptEnv();
	}

	/*
	 * Pause the environment
	 */
	public void pauseEnv()
	{
		envPaused = true;
		interruptEnv();
	}

	/*
	 * Resume the environment
	 */
	public void resumeEnv()
	{
		envPaused = false;
		killFlag = false;
	}
}
