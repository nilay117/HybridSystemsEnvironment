package edu.ucsc.cross.hse.core.processing.event;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;

import bs.commons.objects.access.Protected;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.framework.models.HybridSystem;
import edu.ucsc.cross.hse.core.procesing.io.SystemConsole;
import edu.ucsc.cross.hse.core.processing.execution.HybridEnvironment;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessingElement;

public class ExecutionMonitor extends ProcessingElement
{

	private Thread thread;

	public ExecutionMonitor(CentralProcessor processor)
	{
		super(processor);

	}

	public boolean runSim(boolean run_threadded)
	{

		if (run_threadded)
		{
			thread = new Thread(getSimTask());
			thread.start();

		} else
		{
			launchEnvironment();
		}
		return !this.getInterruptHandler().isTerminating();
	}

	private Runnable getSimTask()
	{
		Runnable task = new Runnable()
		{

			@Override
			public void run()
			{
				launchEnvironment();
			}

		};
		return task;
	}

	public FirstOrderIntegrator getIntegrator()
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
		getEventHandlers(integrator);
		return integrator;
	}

	private void getEventHandlers(FirstOrderIntegrator integrator)
	{
		integrator.addEventHandler(this.getJumpEvaluator(), getSettings().getComputationSettings().ehMaxCheckInterval,
		getSettings().getComputationSettings().ehConvergence,
		getSettings().getComputationSettings().ehMaxIterationCount);
		integrator.addEventHandler(this.getInterruptHandler(), getSettings().getComputationSettings().odeMinStep,
		0.0001, getSettings().getComputationSettings().ehMaxIterationCount);
	}

	public void launchEnvironment()
	{
		long startTime = System.currentTimeMillis();
		this.getConsole().print("Starting Environment Trial");
		FirstOrderIntegrator integrator = getIntegrator();
		double[] y = getComputationEngine().getODEValueVector();
		FirstOrderDifferentialEquations ode = getComputationEngine();
		runIntegrator(integrator, ode, 0.0, getSettings().getExecutionSettings().simDuration, y);
		this.getConsole().print("Environment Trial Complete - Runtime = "
		+ Double.valueOf(((System.currentTimeMillis() - startTime))) / 1000.0 + " seconds");
		getFileParser().autoStoreData(getEnv());

	}

	private Double runIntegrator(FirstOrderIntegrator integrator, FirstOrderDifferentialEquations ode,
	Double start_time, Double duration, double[] ode_vector)
	{
		Double endTime = 0.0;
		// getComponents().performAllTasks(true);
		while (endTime < duration && !this.getInterruptHandler().isTerminating()
		&& getEnv().getJumpIndex() < getSettings().getExecutionSettings().jumpLimit)
		{
			endTime = recursiveIntegrator(getIntegrator(), getComputationEngine(), 0);
		}

		return endTime;
	}

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

			// e.printStackTrace();
			boolean problemResolved = false;
			problemResolved = problemResolved || handleStepSizeIssues(e);
			problemResolved = problemResolved || handleBracketingIssues(e);
			problemResolved = problemResolved || handleEhCountIssues(e);
			printOutUnresolvedIssues(e, problemResolved);
			handleFatalError(e);

			if (recursion_level < getSettings().getComputationSettings().maxRecursiveStackSize
			&& !this.getInterruptHandler().isTerminating())
			{
				return recursiveIntegrator(getIntegrator(), ode, recursion_level + 1);
			} else
			{
				return getEnvironmentOperator().getEnvironmentHybridTime().getTime();
			}

		}

	}

	private void handleFatalError(Exception exc)
	{
		if (this.getSettings().getExecutionSettings().rerunOnFatalErrors)
		{

			if (exc.getClass().equals(NumberIsTooSmallException.class)
			|| exc.getClass().equals(TooManyEvaluationsException.class))
			{

				this.getInterruptHandler().killSim(true);
			}
		}
	}

	private boolean handleStepSizeIssues(Exception exc)
	{
		boolean handledIssue = false;
		if (exc.getClass().equals(NumberIsTooSmallException.class))
		{
			this.getConsole()
			.print("Integrator failure due to large step size - adjusting step size and restarting integrator");
			// this.getSettings()
			// .getComputationSettings().odeMaxStep =
			// this.getSettings().getComputationSettings().odeMaxStep
			// /
			// this.getSettings().getComputationSettings().stepSizeReductionFactor;
			this.getSettings()
			.getComputationSettings().odeMinStep = this.getSettings().getComputationSettings().odeMinStep
			/ (5 * (this.getSettings().getComputationSettings().stepSizeReductionFactor));
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
			"Integrator failure due to large exception handling thresholds - adjusting thresholds and restarting integrator");
			getSettings()
			.getComputationSettings().ehMaxCheckInterval = getSettings().getComputationSettings().ehMaxCheckInterval
			/ getSettings().getComputationSettings().handlingThresholdReductionFactor;
			getSettings().getComputationSettings().ehConvergence = getSettings().getComputationSettings().ehConvergence
			/ getSettings().getComputationSettings().handlingThresholdReductionFactor;
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
			"Integrator failure due to maximal event iterations - adjusting thresholds and restarting integrator");
			getSettings()
			.getComputationSettings().ehMaxIterationCount = getSettings().getComputationSettings().ehMaxIterationCount
			* 2;
			handledIssue = true;
		}
		return handledIssue;
	}

	private void printOutUnresolvedIssues(Exception exc, boolean resolved)
	{
		if (!resolved)
		{
			this.getConsole().print("Integrator failure due to another cause - please see stack trace for details");
			exc.printStackTrace();
		}
	}

	public Thread getThread()
	{
		return thread;
	}

}
