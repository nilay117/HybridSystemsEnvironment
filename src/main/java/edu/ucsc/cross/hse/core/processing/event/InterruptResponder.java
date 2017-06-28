package edu.ucsc.cross.hse.core.processing.event;

import org.apache.commons.math3.ode.events.EventHandler;

import edu.ucsc.cross.hse.core.processing.execution.HybridEnvironment;
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
	}

	/*
	 * The Threshold checking method that will trigger an interrupt
	 */
	@Override
	public double g(double t, double[] y)
	{
		if (outOfDomain())
		{
			killSim();
		}
		//System.out.println(outOfDomains());
		if (killFlag)// || outOfDomain())
		{
			//	this.getComputationEngine().zeroAllDerivatives();
			return -1;
		} else
		{
			return 1;
		}
	}

	public boolean outOfDomain()
	{
		return (!(this.getComponentOperator(getEnv()).isJumpOccurring()
		|| this.getComponentOperator(getEnv()).isFlowOccurring()));
	}

	/*
	 * response to process being terminated
	 */
	@Override
	public Action eventOccurred(double t, double[] y, boolean increasing)
	{
		if (killFlag || outOfDomain())
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
