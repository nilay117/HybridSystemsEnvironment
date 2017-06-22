package edu.ucsc.cross.hse.core.processing.event;

import org.apache.commons.math3.ode.events.EventHandler;

import edu.ucsc.cross.hse.core.processing.execution.Environment;
import edu.ucsc.cross.hse.core.processing.execution.Processor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

/*
 * This class is used to terminate the environment early if need be.
 */
public class InterruptHandler extends ProcessorAccess implements EventHandler
{

	private boolean killFlag; // flag that indactes it is wile to kill the
								// process

	/*
	 * Constructor that uses the environment
	 */
	InterruptHandler(Processor processor)
	{
		super(processor);
	}

	/*
	 * Initialization if values were to be used to trigger events
	 */
	@Override
	public void init(double t0, double[] y0, double t)
	{
	}

	/*
	 * The Threshold checking method that will trigger an interrupt
	 */
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

	/*
	 * response to process being terminated
	 */
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
	public void killSim()
	{
		killFlag = true;
	}
}
