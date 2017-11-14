package edu.ucsc.cross.hse.core.monitor;

import edu.ucsc.cross.hse.core.operator.ExecutionOperator;
import org.apache.commons.math3.ode.events.EventHandler;

/*
 * Continuously monitors the system to interrupt the system upon each jump detected. This allows the ODE to function
 * smoothly as the discontinuities are addressed discretely while the solver is paused
 */
public class EventMonitor implements EventHandler
{

	public Double toggleFlag; // toggle value that switches sign if an event has occurred since the last check

	private boolean approachingJump; // flag indicating that the environment is approaching a jump, meaning that a jump
										// has been detected by the ode but the pre-jump value has not been finalized

	private ExecutionOperator manager; // manager of the environment

	private boolean running; // flag indicating that the environment is terminating

	/*
	 * Response that occurs when event is detecteds
	 */
	@Override
	public EventHandler.Action eventOccurred(double t, double[] y, boolean increasing)
	{
		approachingJump = false;

		if (!running || (manager.getExecutionContent().getHybridSimTime()
		.getJumps() >= manager.getExecutionParameters().maximumJumps))
		{

			return EventHandler.Action.STOP; // continue if jump limit
												// hasn't been reached
		} else
		{
			manager.getDataManager().performDataActions(t, y, JumpStatus.JUMP_DETECTED);
			return EventHandler.Action.RESET_STATE; // otherwise terminate the
			// environment
		}
	}

	/*
	 * The Threshold checking method that will trigger an interrupt
	 */
	@Override
	public double g(double t, double[] y)
	{
		manager.getDataManager().performDataActions(t, y, getCheckJumpStatus());
		return getFlag();
	}

	public JumpStatus getCheckJumpStatus()
	{
		if (approachingJump || running)
		{
			return JumpStatus.APPROACHING_JUMP;
		} else
		{
			return JumpStatus.NO_JUMP;
		}
	}

	public double getFlag()
	{
		boolean jumpOccurring = manager.getSystemControl().checkDomain(true);
		approachingJump = (approachingJump || jumpOccurring);

		if (jumpOccurring || !running)
		{
			// toggleCount = toggleCount + 1;
			toggleFlag = -1 * toggleFlag;
		}

		return toggleFlag;
	}

	/*
	 * Initializes the event handler
	 */
	@Override
	public void init(double t0, double[] y0, double t)
	{
		approachingJump = false;
	}

	/*
	 * Performs actions necessary to reset the state after a jump has occurred
	 */

	public boolean isApproachingJump()
	{
		return approachingJump;
	}

	public boolean isRunning()
	{
		return running;
	}

	@Override
	public void resetState(double t, double[] y)
	{
		manager.getDataManager().performDataActions(t, y, JumpStatus.JUMP_DETECTED);
		executeJumps(t, y);
	}

	public void setRunning(boolean running)
	{
		this.running = running;
	}

	private void executeJumps(double t, double[] y)
	{
		while (manager.getSystemControl().checkDomain(true))
		{
			if (manager.getExecutionContent().getHybridSimTime()
			.getJumps() < manager.getSettings().getExecutionParameters().maximumJumps)
			{
				manager.getSystemControl().applyDynamics(true); // execute all jumps
				manager.getDataManager().performDataActions(t, y, JumpStatus.JUMP_OCCURRED);
			} else
			{
				return;
			}

		}
	}

	/*
	 * Constructor to link the environment
	 */
	public EventMonitor(ExecutionOperator manager)
	{
		this.manager = manager;
		toggleFlag = 1.0;
	}
}
