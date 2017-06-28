package edu.ucsc.cross.hse.core.processing.event;

import org.apache.commons.math3.ode.events.EventHandler;

import edu.ucsc.cross.hse.core.processing.execution.HybridEnvironment;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.models.HybridSystem;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessingElement;

/*
 * This class is used to terminate the environment early if need be.
 */
public class InterruptResponder extends ProcessingElement implements EventHandler
{

	public boolean isTerminating()
	{
		return killFlag;
	}

	private boolean killFlag; // flag that indactes it is wile to kill the
								// process

	private boolean outsideDomainError;

	/*
	 * Constructor that uses the environment
	 */
	public InterruptResponder(CentralProcessor processor)
	{
		super(processor);
		killFlag = false;
	}

	/*
	 * Initialization if values were to be used to trigger events
	 */
	@Override
	public void init(double t0, double[] y0, double t)
	{
		// killFlag = false;
	}

	/*
	 * The Threshold checking method that will trigger an interrupt
	 */
	@Override
	public double g(double t, double[] y)
	{

		// System.out.println(this.getComponents().outOfAllDomains());
		if (killFlag)// ||
						// outOfDomain())
		{
			// this.getComputationEngine().zeroAllDerivatives();
			return -.0000000001;
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
		System.out.println("here");
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
	public void killSim()
	{
		killFlag = true;
	}
}
