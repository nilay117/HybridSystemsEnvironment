package edu.ucsc.cross.hse.core.processing.event;

import org.apache.commons.math3.ode.events.EventHandler;

import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.processing.execution.HybridEnvironment;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessingElement;

public class JumpEvaluator extends ProcessingElement implements EventHandler
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
		getEnvironmentOperator().getEnvironmentHybridTime().setTime(t);

		getComputationEngine().updateValues(y);
		if (ComponentOperator.getConfigurer(getEnv()).isJumpOccurring())
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
		getEnvironmentOperator().getEnvironmentHybridTime().setTime(t);
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
		getEnvironmentOperator().getEnvironmentHybridTime().setTime(t);
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