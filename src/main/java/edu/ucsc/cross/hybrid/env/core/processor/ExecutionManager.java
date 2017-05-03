package edu.ucsc.cross.hybrid.env.core.processor;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;

import bs.commons.io.system.IO;
import bs.commons.objects.access.Protected;

public class ExecutionManager extends ProcessorComponent
{

	private Thread thread;
	private ActionEvaluator jumpHandler;
	private InterruptHandler terminator;

	ExecutionManager(Environment processor)
	{
		super(processor);
		jumpHandler = new ActionEvaluator(processor);
		terminator = new InterruptHandler(processor);
	}

	public void runSim(boolean run_threadded)
	{

		if (run_threadded)
		{
			thread = new Thread(getSimTask());
			thread.start();

		} else
		{
			//runSimulation();
			launchEnvironment();
		}

	}

	public void runSim(Protected<Integer> running_processes)
	{

		thread = new Thread(getSimTask(running_processes));
		thread.start();

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

	private Runnable getSimTask(Protected<Integer> running_processes)
	{
		Runnable task = new Runnable()
		{

			@Override
			public void run()
			{
				launchEnvironment(running_processes);
			}
		};
		return task;
	}

	public FirstOrderIntegrator getIntegrator()
	{
		FirstOrderIntegrator integrator = null;
		switch (getSettings().computation().integrator)
		{
		case EULER:
			integrator = new EulerIntegrator(getSettings().computation().odeMinStep);
			break;
		case DORMAND_PRINCE_853:
			integrator = new DormandPrince853Integrator(getSettings().computation().odeMinStep,
			getSettings().computation().odeMaxStep, getSettings().computation().odeScalAbsoluteTolerance,
			getSettings().computation().odeScalRelativeTolerance);
			break;
		case DORMAND_PRINCE_54:
			integrator = new DormandPrince54Integrator(getSettings().computation().odeMinStep,
			getSettings().computation().odeMaxStep, getSettings().computation().odeScalAbsoluteTolerance,
			getSettings().computation().odeScalRelativeTolerance);
			break;
		}
		getEventHandlers(integrator);
		return integrator;
	}

	private void getEventHandlers(FirstOrderIntegrator integrator)
	{
		integrator.addEventHandler(jumpHandler, getSettings().computation().ehMaxCheckInterval,
		getSettings().computation().ehConvergence, getSettings().computation().ehMaxIterationCount);
		integrator.addEventHandler(terminator, getSettings().computation().ehMaxCheckInterval,
		getSettings().computation().ehConvergence, getSettings().computation().ehMaxIterationCount);
	}

	public void launchEnvironment()
	{
		launchEnvironment(null);

	}

	public void launchEnvironment(Protected<Integer> running_processes)
	{
		long startTime = System.currentTimeMillis();
		IO.out("Starting Environment Trial");
		FirstOrderIntegrator integrator = getIntegrator();
		double[] y = getComputationEngine().getODEValueVector();
		FirstOrderDifferentialEquations ode = getComputationEngine();
		runIntegrator(integrator, ode, 0.0, getSettings().trial().simDuration, y);
		IO.out("Environment Trial Complete - Runtime = "
		+ Double.valueOf(((System.currentTimeMillis() - startTime))) / 1000.0 + " seconds");
		getSaveUtility().autoStoreData(getEnvironment());
		if (running_processes != null)
		{
			System.out.println(running_processes.get());
			running_processes.set(running_processes.get() - 1);
			System.out.println(running_processes.get());
		}

	}

	private Double runIntegrator(FirstOrderIntegrator integrator, FirstOrderDifferentialEquations ode,
	Double start_time, Double duration, double[] ode_vector)
	{
		Double endTime = 0.0;
		getComponents().performTasks(true);
		while (endTime < duration)
		{
			endTime = recursiveIntegrator(getIntegrator(), getComputationEngine(), 0);
		}
		;
		return endTime;
	}

	private Double recursiveIntegrator(FirstOrderIntegrator integrator, FirstOrderDifferentialEquations ode,
	Integer recursion_level)
	{
		try
		{
			Double stopTime = integrator.integrate(ode, getEnvironment().time().seconds(),
			getComputationEngine().getODEValueVector(), getSettings().trial().simDuration,
			getComputationEngine().getODEValueVector());
			return stopTime;
		} catch (Exception e)
		{
			//e.printStackTrace();
			boolean problemResolved = false;
			problemResolved = problemResolved || handleStepSizeIssues(e);
			problemResolved = problemResolved || handleBracketingIssues(e);
			printOutUnresolvedIssues(e, problemResolved);
			getComponents().performTasks(true);
			if (recursion_level < getSettings().computation().maxRecursiveStackSize)
			{
				return recursiveIntegrator(getIntegrator(), ode, recursion_level + 1);
			} else
			{
				return getEnvironment().time().seconds();
			}

		}

	}

	private boolean handleStepSizeIssues(Exception exc)
	{
		boolean handledIssue = false;
		if (exc.getClass().equals(NumberIsTooSmallException.class))
		{
			IO.warn("Integrator failure due to large step size - adjusting step size and restarting integrator");
			getSettings().computation().odeMaxStep = getSettings().computation().odeMaxStep / 2;
			getSettings().computation().odeMinStep = getSettings().computation().odeMinStep / 2;
			handledIssue = true;
		}
		return handledIssue;

	}

	private boolean handleBracketingIssues(Exception exc)
	{
		boolean handledIssue = false;
		if (exc.getClass().equals(NoBracketingException.class))
		{
			IO.warn(
			"Integrator failure due to large exception handling thresholds - adjusting thresholds and restarting integrator");
			getEnvironment().getSettings()
			.computation().ehConvergence = getEnvironment().getSettings().computation().ehConvergence / 1.5;
			getEnvironment().getSettings()
			.computation().ehMaxCheckInterval = getEnvironment().getSettings().computation().ehMaxCheckInterval / 1.5;
			handledIssue = true;
		}
		return handledIssue;
	}

	private void printOutUnresolvedIssues(Exception exc, boolean resolved)
	{
		if (!resolved)
		{
			IO.warn("Integrator failure due to another cause - please see stack trace for details");
			exc.printStackTrace();
		}
	}

	public InterruptHandler getTerminator()
	{
		return terminator;
	}

	public Thread getThread()
	{
		return thread;
	}

}
