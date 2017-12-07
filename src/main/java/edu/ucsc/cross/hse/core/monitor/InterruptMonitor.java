package edu.ucsc.cross.hse.core.monitor;

import edu.ucsc.cross.hse.core.engine.ExecutionEngine;
import edu.ucsc.cross.hse.core.io.Console;
import org.apache.commons.math3.ode.events.EventHandler;

/*
 * Continuously monitors the environment to interrupt the execution when the time or jump threshold is reached, or a
 * manual halt has been requested. This allows the ODE to stop automatically without input from the engine.
 */
public class InterruptMonitor implements EventHandler
{

	private ExecutionEngine manager; // manager of the environment
	private Double endTime;
	private boolean paused;

	public boolean isPaused()
	{
		return paused;
	}

	/*
	 * Response that occurs when event is detecteds
	 */
	@Override
	public EventHandler.Action eventOccurred(double t, double[] y, boolean increasing)
	{

		return EventHandler.Action.STOP; // Terminate integrator

	}

	/*
	 * The Threshold checking method that will trigger an interrupt
	 */
	@Override
	public double g(double t, double[] y)
	{
		Console.printInfoStatus(manager);
		if (isRunning())
		{
			return 1;
		} else
		{
			return -1;
		}
	}

	/*
	 * Initializes the event handler
	 */
	@Override
	public void init(double t0, double[] y0, double t)
	{
		endTime = manager.getExecutionParameters().maximumTime;
		if (manager.getSettings().getInterfaceSettings().runInRealTime)
		{
			endTime += endTime + .000000001 * (System.nanoTime());
		}
		paused = false;
	}

	/*
	 * Performs actions necessary to reset the state after a jump has occurred
	 */
	public boolean isRunning()
	{
		return !paused && !thresholdExceeded();
	}

	public boolean thresholdExceeded()
	{
		Double timeOverThreshold = manager.getExecutionContent().getSimulationTime() - endTime;
		Integer jumpsOverThreshold = manager.getExecutionContent().getHybridSimTime().getJumps()
		- manager.getExecutionParameters().maximumJumps;
		if (timeOverThreshold > 0 && jumpsOverThreshold > 0)
		{
			return true;
		} else
		{
			return false;
		}
	}

	@Override
	public void resetState(double t, double[] y)
	{
	}

	public void setPaused(boolean paused)
	{
		this.paused = paused;
	}

	/*
	 * Constructor to link the environment
	 */
	public InterruptMonitor(ExecutionEngine manager)
	{
		this.manager = manager;
		paused = false;
	}
}
