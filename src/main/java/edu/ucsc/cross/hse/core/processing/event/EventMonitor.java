package edu.ucsc.cross.hse.core.processing.event;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;

import bs.commons.objects.access.Protected;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.processing.execution.Environment;
import edu.ucsc.cross.hse.core.processing.execution.Processor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

public class EventMonitor extends ProcessorAccess
{

	private Thread thread;
	private JumpEvaluator jumpHandler;
	private EarlyTerminator terminator;

	public EventMonitor(Processor processor)
	{
		super(processor);
		jumpHandler = new JumpEvaluator(processor);
		terminator = new EarlyTerminator(processor);
	}

	public void runSim(boolean run_threadded)
	{

		if (run_threadded)
		{
			// runSim(null);
			thread = new Thread(getSimTask());
			thread.start();

		} else
		{
			// runSim(null);
			// runSimulation();
			thread = new Thread(getSimTask());
			thread.start();
			// launchEnvironment();
		}

	}

	public void runSim(Protected<Integer> running_processes)
	{

		thread = new Thread(getSimTask());// running_processes));
		thread.start();
		if (printStack)
		{
			stackPrinter = new Thread(getPrintTask());
			stackPrinter.start();
		}

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

	private Runnable getPrintTask()
	{
		Runnable task = new Runnable()
		{

			@Override
			public void run()
			{
				while (thread.isAlive())
				{
					printStack();
				}
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
		this.getConsole().print("Starting Environment Trial");
		FirstOrderIntegrator integrator = getIntegrator();
		double[] y = getComputationEngine().getODEValueVector();
		FirstOrderDifferentialEquations ode = getComputationEngine();
		runIntegrator(integrator, ode, 0.0, getSettings().trial().simDuration, y);
		this.getConsole().print("Environment Trial Complete - Runtime = "
		+ Double.valueOf(((System.currentTimeMillis() - startTime))) / 1000.0 + " seconds");
		getFileParser().autoStoreData(getEnvironment());
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
		// getComponents().performAllTasks(true);
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
			Double stopTime = integrator.integrate(ode, getEnvironmentOperator().getEnvironmentHybridTime().getTime(),
			getComputationEngine().getODEValueVector(), getSettings().trial().simDuration,
			getComputationEngine().getODEValueVector());
			return stopTime;
		} catch (Exception e)
		{
			// e.printStackTrace();
			boolean problemResolved = false;
			problemResolved = problemResolved || handleStepSizeIssues(e);
			problemResolved = problemResolved || handleBracketingIssues(e);
			printOutUnresolvedIssues(e, problemResolved);
			// getEnvironment().performTasks(true);//
			// getComponents().performAllTasks(true);
			this.getComponents().performAllTasks(ComponentOperator.getConfigurer(getEnvironment()).jumpOccurring());
			if (recursion_level < getSettings().computation().maxRecursiveStackSize)
			{
				return recursiveIntegrator(getIntegrator(), ode, recursion_level + 1);
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
			.print("Integrator failure due to large step size - adjusting step size and restarting integrator");
			getSettings().computation().odeMaxStep = getSettings().computation().odeMaxStep
			/ getSettings().computation().stepSizeReductionFactor;
			getSettings().computation().odeMinStep = getSettings().computation().odeMinStep
			/ getSettings().computation().stepSizeReductionFactor;
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
			getSettings().computation().ehConvergence = getSettings().computation().ehConvergence
			/ getSettings().computation().handlingThresholdReductionFactor;
			getSettings().computation().ehMaxCheckInterval = getSettings().computation().ehMaxCheckInterval
			/ getSettings().computation().handlingThresholdReductionFactor;
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

	public EarlyTerminator getTerminator()
	{
		return terminator;
	}

	public Thread getThread()
	{
		return thread;
	}

	// Debugger

	private Boolean printStack = false;
	private Thread stackPrinter;
	long lastPrint = System.currentTimeMillis();

	private void printStack()
	{
		if (System.nanoTime() > lastPrint)
		{
			lastPrint = System.nanoTime() + 100000000;
			StackTraceElement[] stack = thread.getStackTrace();
			String stackString = stack[0].getClassName();
			for (Integer ele = 1; ele < stack.length; ele++)
			{
				// if (stack[ele].getClassName().contains("edu"))
				{
					stackString += ", " + stack[ele].getClassName();
				}
			}
			if (stackString.contains("XMLParser"))
			{
				System.out.println(stackString);
			}
		}
	}

}
