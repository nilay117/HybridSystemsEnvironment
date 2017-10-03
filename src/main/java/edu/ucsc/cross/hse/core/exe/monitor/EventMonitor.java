package edu.ucsc.cross.hse.core.exe.monitor;

import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.exe.operator.EnvironmentManager;
import org.apache.commons.math3.ode.events.EventHandler;

/*
 * Continuously monitors the system to interrupt the system upon each jump detected. This allows the ODE to function
 * smoothly as the discontinuities are addressed discretely while the solver is paused
 */
@Loggable(Loggable.TRACE)
public class EventMonitor implements EventHandler
{

	private EnvironmentManager manager; // manager of the environment

	public Integer toggleCount = 0; // number of times toggle value has switched signs

	private boolean approachingJump; // flag indicating that the environment is approaching a jump, meaning that a jump
										// has been detected by the ode but the pre-jump value has not been finalized

	private boolean approachingTermination; // flag indicating that the environment is terminating

	public Double toggleFlag; // toggle value that switches sign if an event has occurred since the last check

	/*
	 * Constructor to link the environment
	 */
	public EventMonitor(EnvironmentManager manager)
	{
		this.manager = manager;
		toggleFlag = 1.0;
	}

	public double getFlag()
	{
		boolean jumpOccurring = manager.getSystemControl().checkDomain(true);
		approachingJump = (approachingJump || jumpOccurring);

		if (jumpOccurring || approachingTermination)
		{
			toggleCount = toggleCount + 1;
			toggleFlag = -1 * toggleFlag;
		}

		return toggleFlag;
	}

	public JumpStatus getCheckJumpStatus()
	{
		if (approachingJump || approachingTermination)
		{
			return JumpStatus.APPROACHING_JUMP;
		} else
		{
			return JumpStatus.NO_JUMP;
		}
	}

	/*
	 * The Threshold checking method that will trigger an interrupt
	 */
	@Override
	public double g(double t, double[] y)
	{
		manager.getConsole().printStatus();
		manager.getDataManager().monitorData(t, y, getCheckJumpStatus());
		// System.out.println(t + " checking " + y[0] + " " + y[1] + toggleFlag);
		return getFlag();
	}

	/*
	 * Response that occurs when event is detecteds
	 */
	@Override
	@Loggable(Loggable.DEBUG)
	public EventHandler.Action eventOccurred(double t, double[] y, boolean increasing)
	{
		approachingJump = false;
		if (Math.floorMod(toggleCount, 2) == 0) // check to see if an odd number of
		// switches occurred
		{
			toggleFlag = -1.0 * toggleFlag; // adjust the flag if so to avoid integrator
			// erors
		}
		toggleCount = 0; // reset the toggle counter

		if (approachingTermination || (manager.getContents().getSimTime()
		.getJumps() >= manager.getSettings().getExecutionParameters().maximumJumps))
		{

			return EventHandler.Action.STOP; // continue if jump limit
												// hasn't been reached
		} else
		{
			return EventHandler.Action.RESET_STATE; // otherwise terminate the
			// environment
		}
	}

	/*
	 * Performs actions necessary to reset the state after a jump has occurred
	 */

	@Override
	public void resetState(double t, double[] y)
	{
		manager.getDataManager().monitorData(t, y, JumpStatus.JUMP_DETECTED);
		executeJumps(t, y);
	}

	private void executeJumps(double t, double[] y)
	{
		manager.getSystemControl().applyDynamics(true); // execute all jumps
		manager.getDataManager().monitorData(t, y, JumpStatus.JUMP_OCCURRED);
		if (manager.getSystemControl().checkDomain(true))
		{
			System.out.println("Multijump");
			executeJumps(t, y);
		}
	}

	/*
	 * Initializes the event handler
	 */
	@Override
	public void init(double t0, double[] y0, double t)
	{
		approachingTermination = false;
		approachingJump = false;
		toggleFlag = 1.0;
	}

	public boolean isApproachingJump()
	{
		return approachingJump;
	}

	public boolean isApproachingTermination()
	{
		return approachingTermination;
	}
}