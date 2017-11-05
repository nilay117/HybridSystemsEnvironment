package edu.ucsc.cross.hse.core.monitor;

import edu.ucsc.cross.hse.core.io.Console;
import edu.ucsc.cross.hse.core.operator.ExecutionOperator;
import edu.ucsc.cross.hse.core.operator.SimulationOperator;
import edu.ucsc.cross.hse.core.setting.ComputationSettings;
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
public class ComputationMonitor
{

	private ExecutionOperator manager;

	public ComputationMonitor(ExecutionOperator manager)
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
		SimulationOperator ode = manager.getSimEngine();
		double[] y = manager.getExecutionContent().getValueVector();
		runIntegrator(integrator, ode, manager.getExecutionContent().getHybridSimTime().getTime(),
		manager.getExecutionParameters().maximumTime, y);
		// runIntegrator(integrator, ode, manager.getExecutionContent().getHybridSimTime().getTime(),
		// manager.getExecutionParameters().getMaximumTime(), y);
		runTime += Double.valueOf(((System.currentTimeMillis() - startTime))) / 1000.0;

	}

	/*
	 * Instantiates the simulation integrator based on the current settings
	 */
	protected FirstOrderIntegrator getSimulationIntegrator()
	{
		FirstOrderIntegrator integrator = null;
		switch (manager.getSettings().getComputationSettings().integratorType)
		{
			case EULER:
				integrator = new EulerIntegrator(manager.getSettings().getComputationSettings().odeMinimumStepSize);
				break;
			case DORMAND_PRINCE_853:
				integrator = new DormandPrince853Integrator(
				manager.getSettings().getComputationSettings().odeMinimumStepSize,
				manager.getSettings().getComputationSettings().odeMaximumStepSize,
				manager.getSettings().getComputationSettings().odeSolverAbsoluteTolerance,
				manager.getSettings().getComputationSettings().odeRelativeTolerance);
				break;
			case DORMAND_PRINCE_54:
				integrator = new DormandPrince54Integrator(
				manager.getSettings().getComputationSettings().odeMinimumStepSize,
				manager.getSettings().getComputationSettings().odeMaximumStepSize,
				manager.getSettings().getComputationSettings().odeSolverAbsoluteTolerance,
				manager.getSettings().getComputationSettings().odeRelativeTolerance);
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
		manager.getSettings().getComputationSettings().eventHandlerMaximumCheckInterval,
		manager.getSettings().getComputationSettings().eventHandlerConvergenceThreshold,
		manager.getSettings().getComputationSettings().maxEventHandlerIterations);
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
			timeExpired = endTime >= manager.getExecutionParameters().maximumTime;
			jumpsExpired = manager.getExecutionContent().getHybridSimTime()
			.getJumps() >= manager.getExecutionParameters().maximumJumps;
			terminated = !manager.getJumpEvaluator().isRunning();

		}
		manager.getDataManager().gatherData(endTime, manager.getExecutionContent().getValueVector(), JumpStatus.NO_JUMP,
		true);
		return endTime;
	}

	/*
	 * 
	 * /* Executes the integrator recursively, calling itself after an error occurs or returning the end time if the
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
		return recursiveIntegrator(recursion_level, manager.getExecutionParameters().maximumTime);
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
	public Double recursiveIntegrator(Integer recursion_level, Double end_time)
	{
		try
		{
			FirstOrderIntegrator integrator = getSimulationIntegrator();

			Double stopTime = integrator.integrate(manager.getSimEngine(),
			manager.getExecutionContent().getHybridSimTime().getTime(), manager.getExecutionContent().getValueVector(),
			end_time, manager.getExecutionContent().getValueVector());
			return stopTime;
		} catch (Exception e)
		{
			manager.getDataManager().revertToLastStoredValue(manager.getExecutionContent().getSimulationTime());
			boolean problemResolved = false;
			problemResolved = problemResolved || handleStepSizeIssues(e);
			problemResolved = problemResolved || handleBracketingIssues(e);
			problemResolved = problemResolved || handleEhCountIssues(e);

			printOutUnresolvedIssues(e, problemResolved);
			// handleFatalError(e);

			if (recursion_level < ComputationSettings.maximumRecursiveIntegratorCalls)// !this.getInterruptHandler().isTerminating())
			{

				return recursiveIntegrator(recursion_level + 1, end_time);
			} else
			{
				return manager.getExecutionContent().getHybridSimTime().getTime();
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
			if (manager.getSettings().getLogSettings().printIntegratorExceptions)
			{
				Console
				.warn("Integrator failure - step size too large - adjusting step size and restarting integrator");
			}
			manager.getSettings()
			.getComputationSettings().odeMaximumStepSize = this.manager.getSettings()
			.getComputationSettings().odeMaximumStepSize
			* this.manager.getSettings().getComputationSettings().stepSizeErrorMaxCorrectionFactor;
			manager.getSettings()
			.getComputationSettings().odeMinimumStepSize = this.manager.getSettings()
			.getComputationSettings().odeMinimumStepSize
			* (this.manager.getSettings().getComputationSettings().stepSizeErrorMinCorrectionFactor);
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
			if (manager.getExecutionContent()
			.getSimulationTime() > (manager.getSettings().getComputationSettings().odeMaximumStepSize))
			{
				if (manager.getSettings().getLogSettings().printIntegratorExceptions)
				{
					Console.warn(manager.getExecutionContent().getSimulationTime() + " "
					+ "Integrator exception - exception handling thresholds too large - adjusting intervals and restarting integrator");// getEnvironmentOperator().getEnvironmentHybridTime().incrementJumpIndex(0);
				}
				manager.getSettings()
				.getComputationSettings().eventHandlerMaximumCheckInterval = manager.getSettings()
				.getComputationSettings().eventHandlerMaximumCheckInterval
				* manager.getSettings().getComputationSettings().intervalErrorCorrectionFactor;
				manager.getSettings()
				.getComputationSettings().eventHandlerConvergenceThreshold = manager.getSettings()
				.getComputationSettings().eventHandlerConvergenceThreshold
				* manager.getSettings().getComputationSettings().convergenceErrorCorrectionFactor;
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
			if (manager.getSettings().getLogSettings().printIntegratorExceptions)
			{
				Console.warn(
				"Integrator exception - maximum event handler iterations reached - adjusting maximum iterations and restarting integrator");
			}
			manager.getSettings().getComputationSettings().maxEventHandlerIterations = manager.getSettings()
			.getComputationSettings().maxEventHandlerIterations * 2;
			handledIssue = true;
		}
		return handledIssue;
	}

	/*
	 * Determines what to do when a fatal error occurs depending on the settings defined. These errors don't necessarly
	 * mean the data is inacurate, and most likely won't effect the outcome much, however the system can be enabled to
	 * restart a trial upon such errors if desired.
	 */
	/*
	 * private void handleFatalError(Exception exc) {
	 * 
	 * if (exc.getClass().equals(NumberIsTooSmallException.class) ||
	 * exc.getClass().equals(TooManyEvaluationsException.class) || exc.getClass().equals(NoBracketingException.class)) {
	 * 
	 * // this.getInterruptHandler().interruptEnv(true); } }
	 */

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
	// private Double runRealTimeIntegrator(FirstOrderIntegrator integrator, FirstOrderDifferentialEquations ode,
	// Double start_time, Double duration, double[] ode_vector)
	// {
	// Double endTime = 0.0;
	// boolean timeExpired = false;
	// boolean jumpsExpired = false;
	// boolean terminated = false;
	// Double timeAhead = 100.0;
	// Long startTime = System.currentTimeMillis();
	// while (!timeExpired && !jumpsExpired && !terminated)
	// {
	// System.out.println(
	// "Check " + manager.getExecutionContent().getSimulationTime() + (System.currentTimeMillis() - startTime));
	// while ((manager.getExecutionContent().getSimulationTime() + .005)
	// * 1000 > (System.currentTimeMillis() - startTime))
	//
	// {
	// }
	// endTime = recursiveIntegrator(0, manager.getExecutionContent().getSimulationTime() + .005);
	// timeExpired = endTime >= manager.getExecutionParameters().getMaximumTime();
	// jumpsExpired = manager.getExecutionContent().getHybridSimTime().getJumps() >= manager
	// .getExecutionParameters().getMaximumJumps();
	// terminated = !manager.getJumpEvaluator().isRunning();
	//
	// }
	// manager.getDataManager().gatherData(endTime, manager.getExecutionContent().getValueVector(), JumpStatus.NO_JUMP,
	// true);
	// return endTime;
	// }
}
