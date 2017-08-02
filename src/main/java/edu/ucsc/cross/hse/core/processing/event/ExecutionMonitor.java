package edu.ucsc.cross.hse.core.processing.event;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;

import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

/*
 * Executes and monitors an instance of the Hybrid Environment. Errors are
 * caught and handled accordingly to keep environment running and protect result
 * data if a fatal error occurs. Also adjusts settings to reduce future errors
 * and can automatically restart environment when errors that may effect results
 * occur. Allowing adjustments to be made eventually leads to a configuration
 * that does not result in any errors, which can be saved for future use
 */
public class ExecutionMonitor extends ProcessorAccess
{

	private Double runTime; // total time that the environment has been running
							// (excluding pauses and errors)

	/*
	 * get the total run time
	 * 
	 * @return total run time
	 */
	public Double getRunTime()
	{
		return runTime;
	}

	/*
	 * Constructor linking the processor
	 */
	public ExecutionMonitor(CentralProcessor processor)
	{
		super(processor);
		runTime = 0.0;
	}

	/*
	 * Constructor linking the processor
	 */
	public ExecutionMonitor(CentralProcessor processor, Double run_time)
	{
		super(processor);
		runTime = run_time;
	}

	/*
	 * Starts an execution of the current environment
	 */
	public void launchEnvironment()
	{
		long startTime = System.currentTimeMillis();
		FirstOrderIntegrator integrator = getSimulationIntegrator();
		double[] y = getComputationEngine().getODEValueVector();
		FirstOrderDifferentialEquations ode = getComputationEngine();
		runIntegrator(integrator, ode, getEnv().getEnvironmentTime(), getSettings().getExecutionSettings().simDuration,
		y);
		runTime += Double.valueOf(((System.currentTimeMillis() - startTime))) / 1000.0;
	}

	/*
	 * Instantiates the simulation integrator based on the current settings
	 */
	protected FirstOrderIntegrator getSimulationIntegrator()
	{
		FirstOrderIntegrator integrator = null;
		switch (getSettings().getComputationSettings().integrator)
		{
		case EULER:
			integrator = new EulerIntegrator(getSettings().getComputationSettings().odeMinStep);
			break;
		case DORMAND_PRINCE_853:
			integrator = new DormandPrince853Integrator(getSettings().getComputationSettings().odeMinStep,
			getSettings().getComputationSettings().odeMaxStep,
			getSettings().getComputationSettings().odeScalAbsoluteTolerance,
			getSettings().getComputationSettings().odeScalRelativeTolerance);
			break;
		case DORMAND_PRINCE_54:
			integrator = new DormandPrince54Integrator(getSettings().getComputationSettings().odeMinStep,
			getSettings().getComputationSettings().odeMaxStep,
			getSettings().getComputationSettings().odeScalAbsoluteTolerance,
			getSettings().getComputationSettings().odeScalRelativeTolerance);
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
		integrator.addEventHandler(getJumpEvaluator(), getSettings().getComputationSettings().ehMaxCheckInterval,
		getSettings().getComputationSettings().ehConvergence,
		getSettings().getComputationSettings().ehMaxIterationCount);
		integrator.addEventHandler(getInterruptHandler(), getSettings().getComputationSettings().ehMaxCheckInterval,
		getSettings().getComputationSettings().ehConvergence,
		getSettings().getComputationSettings().ehMaxIterationCount);
	}

	/*
	 * Starts the specified integrator for the specified dynamics, start time,
	 * and duration
	 * 
	 * @param integrator - integrator to be used
	 * 
	 * @param ode - set of differential equations that define the dynamical
	 * system
	 * 
	 * @param start_time - initial time when integration will start
	 * 
	 * @param duration - final time when integration will be complete
	 * 
	 * @param ode_vector - initial values of all variables associated with the
	 * ode
	 */
	private Double runIntegrator(FirstOrderIntegrator integrator, FirstOrderDifferentialEquations ode,
	Double start_time, Double duration, double[] ode_vector)
	{
		Double endTime = 0.0;
		// getComponents().performAllTasks(true);
		while ((endTime < duration) && !this.getInterruptHandler().isTerminating()
		&& (getEnv().getJumpIndex() < getSettings().getExecutionSettings().jumpLimit)
		&& (!this.getComponentOperator(getEnv()).outOfAllDomains()))
		{
			endTime = recursiveIntegrator(getSimulationIntegrator(), getComputationEngine(), 0);
		}
		return endTime;

	}

	/*
	 * Executes the integrator recursively, calling itself after an error occurs
	 * or returning the end time if the maximum recursion level has been
	 * reached.
	 * 
	 * @param integrator - integrator to be used
	 * 
	 * @param ode - set of differential equations that define the dynamical
	 * system
	 * 
	 * @param recursion_level - number of times that the method has been called
	 * by itself
	 */
	private Double recursiveIntegrator(FirstOrderIntegrator integrator, FirstOrderDifferentialEquations ode,
	Integer recursion_level)
	{
		try
		{
			Double stopTime = integrator.integrate(ode, getEnv().getEnvironmentTime(),
			getComputationEngine().getODEValueVector(), getSettings().getExecutionSettings().simDuration,
			getComputationEngine().getODEValueVector());
			return stopTime;
		} catch (Exception e)
		{
			if (this.getInterruptHandler().isTerminating())
			{
				return getEnvironmentOperator().getEnvironmentHybridTime().getTime();
			}
			boolean problemResolved = false;
			problemResolved = problemResolved || handleStepSizeIssues(e);
			problemResolved = problemResolved || handleBracketingIssues(e);
			problemResolved = problemResolved || handleEhCountIssues(e);
			printOutUnresolvedIssues(e, problemResolved);
			handleFatalError(e);

			if (recursion_level < getSettings().getComputationSettings().maxRecursiveStackSize
			&& !this.getInterruptHandler().isTerminating())
			{
				return recursiveIntegrator(getSimulationIntegrator(), ode, recursion_level + 1);
			} else
			{
				return getEnvironmentOperator().getEnvironmentHybridTime().getTime();
			}

		}

	}

	private boolean handleStepSizeIssues(Exception exc)
	{
		boolean handledIssue = false;
		if (exc.getClass().equals(NumberIsTooSmallException.class))
		{
			this.getConsole()
			.print("Integrator failure - step size too large - adjusting step size and restarting integrator");
			this.getSettings()
			.getComputationSettings().odeMaxStep = this.getSettings().getComputationSettings().odeMaxStep
			* this.getSettings().getComputationSettings().stepSizeReduction;
			this.getSettings()
			.getComputationSettings().odeMinStep = this.getSettings().getComputationSettings().odeMinStep
			* (this.getSettings().getComputationSettings().minStepSizeReduction);
			handledIssue = true;
		}
		return handledIssue;

	}

	private boolean handleBracketingIssues(Exception exc)
	{
		boolean handledIssue = false;
		if (exc.getClass().equals(NoBracketingException.class))
		{
			this.getConsole().print(
			"Integrator failure - exception handling thresholds too large  - adjusting intervals and restarting integrator");
			getEnvironmentOperator().getEnvironmentHybridTime().incrementJumpIndex(0);
			getSettings()
			.getComputationSettings().ehMaxCheckInterval = getSettings().getComputationSettings().ehMaxCheckInterval
			* getSettings().getComputationSettings().eventHandlingIntervalReduction;
			getSettings().getComputationSettings().ehConvergence = getSettings().getComputationSettings().ehConvergence
			* getSettings().getComputationSettings().eventHandlingConvergenceReduction;
			handledIssue = true;
		}
		return handledIssue;
	}

	private boolean handleEhCountIssues(Exception exc)
	{
		boolean handledIssue = false;
		if (exc.getClass().equals(TooManyEvaluationsException.class))
		{
			this.getConsole().print(
			"Integrator failure - maximum event handler iterations reached - adjusting maximum iterations and restarting integrator");
			getSettings()
			.getComputationSettings().ehMaxIterationCount = getSettings().getComputationSettings().ehMaxIterationCount
			* 2;
			handledIssue = true;
		}
		return handledIssue;
	}

	private void handleFatalError(Exception exc)
	{
		if (this.getSettings().getExecutionSettings().rerunOnFatalErrors)
		{

			if (exc.getClass().equals(NumberIsTooSmallException.class)
			|| exc.getClass().equals(TooManyEvaluationsException.class))
			{

				this.getInterruptHandler().interruptEnv(true);
			}
		}
	}

	private void printOutUnresolvedIssues(Exception exc, boolean resolved)
	{
		if (!resolved)
		{
			this.getConsole().print("Integrator failure due to another cause - please see stack trace for details");
			exc.printStackTrace();
		}
	}

}
