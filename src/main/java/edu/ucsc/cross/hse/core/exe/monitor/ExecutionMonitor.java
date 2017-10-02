package edu.ucsc.cross.hse.core.exe.monitor;

import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.exe.operator.EnvironmentManager;
import edu.ucsc.cross.hse.core.exe.operator.SimulationEngine;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;

/*
 * Executes and monitors an instance of the Hybrid Environment. Errors are caught and handled accordingly to keep
 * environment running and protect result data if a fatal error occurs. Also adjusts settings to reduce future errors
 * and can automatically restart environment when errors that may effect results occur. Allowing adjustments to be made
 * eventually leads to a configuration that does not result in any errors, which can be saved for future use
 */
@Loggable(Loggable.DEBUG)
public class ExecutionMonitor
{

	private EnvironmentManager manager;

	public ExecutionMonitor(EnvironmentManager manager)
	{

		this.manager = manager;
		runTime = 0.0;
	}

	/*
	 * Total time that the environment has been running (excluding pauses and errors)
	 */
	private Double runTime;

	/*
	 * Get the total run time
	 * 
	 * @return total run time
	 */
	public Double getRunTime()
	{
		return runTime;
	}

	/*
	 * Starts an execution of the current environment
	 */
	public void launchEnvironment()
	{
		long startTime = System.currentTimeMillis();
		FirstOrderIntegrator integrator = getSimulationIntegrator();
		SimulationEngine ode = manager.getSimEngine();
		double[] y = manager.getVector().getValueVector(false);
		runIntegrator(integrator, ode, manager.getContents().getSimTime().getTime(),
		manager.getSettings().getExecutionParameters().simulationDuration, y);
		runTime += Double.valueOf(((System.currentTimeMillis() - startTime))) / 1000.0;
	}

	/*
	 * Instantiates the simulation integrator based on the current settings
	 */
	protected FirstOrderIntegrator getSimulationIntegrator()
	{
		FirstOrderIntegrator integrator = null;
		switch (manager.getSettings().getEnvironmentSettings().integrator)
		{
		case EULER:
			integrator = new EulerIntegrator(manager.getSettings().getEnvironmentSettings().odeMinStep);
			break;
		case DORMAND_PRINCE_853:
			integrator = new DormandPrince853Integrator(manager.getSettings().getEnvironmentSettings().odeMinStep,
			manager.getSettings().getEnvironmentSettings().odeMaxStep,
			manager.getSettings().getEnvironmentSettings().odeScalAbsoluteTolerance,
			manager.getSettings().getEnvironmentSettings().odeScalRelativeTolerance);
			break;
		case DORMAND_PRINCE_54:
			integrator = new DormandPrince54Integrator(manager.getSettings().getEnvironmentSettings().odeMinStep,
			manager.getSettings().getEnvironmentSettings().odeMaxStep,
			manager.getSettings().getEnvironmentSettings().odeScalAbsoluteTolerance,
			manager.getSettings().getEnvironmentSettings().odeScalRelativeTolerance);
			break;
		}
		loadEventHandlers(integrator);
		return integrator;
	}

	/*
	 * Loads the jump evaluator and interrupt responder to an integrator
	 * 
	 * @param integrator - integrator instance to contain the event handlers
	 */
	private void loadEventHandlers(FirstOrderIntegrator integrator)
	{
		integrator.addEventHandler(manager.getJumpEvaluator(),
		manager.getSettings().getEnvironmentSettings().ehMaxCheckInterval,
		manager.getSettings().getEnvironmentSettings().ehConvergence,
		manager.getSettings().getEnvironmentSettings().ehMaxIterationCount);
		// integrator.addEventHandler(getInterruptHandler(),
		// manager.getSettings().getEnvironmentSettings().ehMaxCheckInterval,
		// manager.getSettings().getEnvironmentSettings().ehConvergence,
		// manager.getSettings().getEnvironmentSettings().ehMaxIterationCount);
	}

	/*
	 * Starts the specified integrator for the specified dynamics, start time, and duration
	 * 
	 * @param integrator - integrator to be used
	 * 
	 * @param ode - set of differential equations that define the dynamical system
	 * 
	 * @param start_time - initial time when integration will start
	 * 
	 * @param duration - final time when integration will be complete
	 * 
	 * @param ode_vector - initial values of all variables associated with the ode
	 */
	private Double runIntegrator(FirstOrderIntegrator integrator, FirstOrderDifferentialEquations ode,
	Double start_time, Double duration, double[] ode_vector)
	{
		Double endTime = 0.0;
		boolean timeExpired = false;
		boolean jumpsExpired = false;
		boolean terminated = false;
		while (!timeExpired && !jumpsExpired && !terminated)
		{

			endTime = recursiveIntegrator(0);
			timeExpired = endTime >= manager.getSettings().getExecutionParameters().simulationDuration;
			jumpsExpired = manager.getContents().getSimTime()
			.getJumps() >= manager.getSettings().getExecutionParameters().maximumJumps;
			terminated = manager.getJumpEvaluator().isApproachingTermination();

		}
		return endTime;
	}

	/*
	 * Executes the integrator recursively, calling itself after an error occurs or returning the end time if the
	 * maximum recursion level has been reached.
	 * 
	 * @param integrator - integrator to be used
	 * 
	 * @param ode - set of differential equations that define the dynamical system
	 * 
	 * @param recursion_level - number of times that the method has been called by itself
	 */
	public Double recursiveIntegrator(Integer recursion_level)
	{
		try
		{
			FirstOrderIntegrator integrator = getSimulationIntegrator();

			Double stopTime = integrator.integrate(manager.getSimEngine(), manager.getContents().getSimTime().getTime(),
			manager.getVector().getValueVector(false),
			manager.getSettings().getExecutionParameters().simulationDuration,
			manager.getVector().getValueVector(false));
			return stopTime;
		} catch (Exception e)
		{
			// manager.getContentManager().clearChangeValues();
			// e.printStackTrace();
			// manager.getJumpEvaluator().flag = manager.getJumpEvaluator().flag * -1.0;
			// if (this.getInterruptHandler().isTerminating())
			{
				// return getEnvironmentOperator().getEnvironmentHybridTime().getTime();
			}
			boolean problemResolved = false;
			problemResolved = problemResolved || handleStepSizeIssues(e);
			problemResolved = problemResolved || handleBracketingIssues(e);
			problemResolved = problemResolved || handleEhCountIssues(e);
			// printOutUnresolvedIssues(e, problemResolved);
			// handleFatalError(e);
			// System.out.println(XMLParser.serializeObject(manager.getSimEngine().getODEValueVector()));
			// System.exit(1);
			if (recursion_level < manager.getSettings().getEnvironmentSettings().maxRecursiveStackSize)// !this.getInterruptHandler().isTerminating())
			{

				return recursiveIntegrator(recursion_level + 1);
			} else
			{
				// System.out.println("wtf");
				return manager.getContents().getSimTime().getTime();
			}

		}

	}

	/*
	 * Catches halts in the integrator due to step size errors, and makes adjustments before restarting the integrator
	 * to reduce the chance of similar errors in the future
	 */
	private boolean handleStepSizeIssues(Exception exc)
	{
		boolean handledIssue = false;
		if (exc.getClass().equals(NumberIsTooSmallException.class))
		{
			// this.getConsole()
			// .print("Integrator failure - step size too large - adjusting step size and restarting integrator");
			manager.getSettings()
			.getEnvironmentSettings().odeMaxStep = this.manager.getSettings().getEnvironmentSettings().odeMaxStep
			* this.manager.getSettings().getEnvironmentSettings().stepSizeReduction;
			manager.getSettings()
			.getEnvironmentSettings().odeMinStep = this.manager.getSettings().getEnvironmentSettings().odeMinStep
			* (this.manager.getSettings().getEnvironmentSettings().minStepSizeReduction);
			handledIssue = true;
		}
		return handledIssue;

	}

	/*
	 * Catches halts in the integrator due to bracketing issues (minimum check inverval too high), and makes adjustments
	 * before restarting the integrator to reduce the chance of similar errors in the future
	 */
	private boolean handleBracketingIssues(Exception exc)
	{
		boolean handledIssue = false;
		if (exc.getClass().equals(NoBracketingException.class))
		{
			if (manager.getTimeOperator()
			.getSimulationTime() > (manager.getSettings().getEnvironmentSettings().odeMaxStep))
			{
				System.out.println(manager.getTimeOperator().getSimulationTime() + " "
				+ "Integrator failure - exception handling thresholds too large - adjusting intervals and restarting integrator");// getEnvironmentOperator().getEnvironmentHybridTime().incrementJumpIndex(0);
				manager.getSettings()
				.getEnvironmentSettings().ehMaxCheckInterval = manager.getSettings()
				.getEnvironmentSettings().ehMaxCheckInterval
				* manager.getSettings().getEnvironmentSettings().eventHandlingIntervalReduction;
				manager.getSettings()
				.getEnvironmentSettings().ehConvergence = manager.getSettings().getEnvironmentSettings().ehConvergence
				* manager.getSettings().getEnvironmentSettings().eventHandlingConvergenceReduction;
			}
			handledIssue = true;
		}
		return handledIssue;
	}

	/*
	 * Catches halts in the integrator due to the maximum number of event handling iterations being reached, and makes
	 * adjustments before restarting the integrator to reduce the chance of similar errors in the future
	 */
	private boolean handleEhCountIssues(Exception exc)
	{
		boolean handledIssue = false;
		if (exc.getClass().equals(TooManyEvaluationsException.class))
		{
			// this.getConsole().print(
			// "Integrator failure - maximum event handler iterations reached - adjusting maximum iterations and
			// restarting integrator");
			manager.getSettings().getEnvironmentSettings().ehMaxIterationCount = manager.getSettings()
			.getEnvironmentSettings().ehMaxIterationCount * 2;
			handledIssue = true;
		}
		return handledIssue;
	}

	/*
	 * Determines what to do when a fatal error occurs depending on the settings defined. These errors don't necessarly
	 * mean the data is inacurate, and most likely won't effect the outcome much, however the system can be enabled to
	 * restart a trial upon such errors if desired.
	 */
	private void handleFatalError(Exception exc)
	{
		// if (manager.getSettings().getEnvironmentSettings().rerunOnFatalErrors)
		{

			if (exc.getClass().equals(NumberIsTooSmallException.class)
			|| exc.getClass().equals(TooManyEvaluationsException.class)
			|| exc.getClass().equals(NoBracketingException.class))
			{

				// this.getInterruptHandler().interruptEnv(true);
			}
		}
	}

	/*
	 * Displays information about an error that occured that the monitor was not able to address.
	 */
	private void printOutUnresolvedIssues(Exception exc, boolean resolved)
	{
		if (!resolved)
		{
			// this.getConsole().print("Integrator failure due to another cause - please see stack trace for details");
			exc.printStackTrace();
		}
	}

}
