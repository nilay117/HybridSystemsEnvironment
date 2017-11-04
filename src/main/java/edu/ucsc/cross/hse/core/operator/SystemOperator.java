package edu.ucsc.cross.hse.core.operator;

import com.be3short.io.xml.XMLParser;
import com.be3short.obj.manipulation.DynamicObjectManipulator;
import edu.ucsc.cross.hse.core.io.Console;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import edu.ucsc.cross.hse.core.object.HybridSystem.HybridSystemOperator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SystemOperator
{

	int activeThreads = 0;
	private HashMap<Integer, ArrayList<HybridSystem<?>>> systemz;
	private HashMap<HybridSystem<?>, Runnable> systemzt;
	private ExecutionOperator content;
	private boolean jo;

	public SystemOperator(ExecutionOperator content)
	{
		this.content = content;
	}

	public boolean checkDomain(boolean jump)
	{
		boolean domain = false;
		for (HybridSystem<?> hs : content.getContents().getSystems())
		{
			if (jump)
			{
				domain = domain || HybridSystemOperator.d(hs);
			} else
			{
				domain = domain || HybridSystemOperator.c(hs);
			}
		}
		return domain;
	}

	public void applyDynamics(boolean jump_occurring)
	{
		prepareDynamicComponents(jump_occurring);
		if (content.getSettings().getFunctionalitySettings().runThreadedOperations)
		{
			applyDynamicsMultiThread(jump_occurring);
		} else
		{
			applyDynamicsSingleThread(jump_occurring);
		}
		processDynamicComponents(jump_occurring);
	}

	public void applyDynamicsSingleThread(boolean jump_occurring)
	{
		for (HybridSystem<?> hs : content.getContents().getSystems())
		{
			try
			{
				if (jump_occurring)
				{
					HybridSystemOperator.g(hs);
				} else
				{
					HybridSystemOperator.f(hs);
				}
			} catch (Exception dynamicsError)
			{
				Console.error("Apply Dynamics Error on " + hs.getClass() + " with state: \n"
				+ XMLParser.serializeObject(hs.getState()), dynamicsError);
			}
		}
	}

	public void applyDynamicsMultiThread(boolean jump_occurring)
	{

		// checkSystems();
		jo = jump_occurring;

		// Collection<Runnable> threads;// = new Runnable[content.getContents().getSystems().size()];
		final int NUM_THREADS = Runtime.getRuntime().availableProcessors() - 1;
		// System.out.println(NUM_THREADS);
		final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
		for (int i = 0; i < content.getContents().getSystems().size(); i++)
		{
			executor.execute(prepareThread(content.getContents().getSystems().get(i)));
		}
		// executor.
		// while (!executor.isTerminated())
		// {
		//
		// }
		executor.shutdown();
		while (!executor.isTerminated())
		{

		}
		// {
		// for (HybridSystem<?> hs : content.getContents().getSystems())
		// {
		// // System.out.println("new" + i++);
		// new Thread(prepareThread(hs)).start();
		// ;
		// }
		// }
		// boolean done = false;
		// while (!done)
		// {
		// done = true;
		// for (Boolean val : systemz.values())
		// {
		// done = done && val;
		// }
		// }

	}

	private Runnable prepareThread(HybridSystem<?> hs)
	{
		return new Runnable()
		{

			public void run()
			{

				// systemz.put(hs, false);
				try
				{

					if (jo)
					{
						HybridSystemOperator.g(hs);
					} else
					{
						HybridSystemOperator.f(hs);
					}
				} catch (Exception dynamicsError)
				{
					Console.error("Apply Dynamics Error on " + hs.getClass() + " with state: \n"
					+ XMLParser.serializeObject(hs.getState()), dynamicsError);
				}
				// System.out.println(activeThreads);

				// systemz.put(hs, true);
			}
		};

	}

	public void prepareDynamicComponents(boolean jump)
	{
		if (jump)
		{
			storeChangeValues(true);
		} else
		{
			clearChangeValues();
		}
	}

	public void processDynamicComponents(boolean jump)
	{
		// if jump has occurred (ode has settled state and is paused)
		if (jump)
		{
			storeChangeValues(false); // store the change values to the state
			clearChangeValues(); // clear the change variables

		}

	}

	public void clearChangeValues()
	{
		for (DynamicObjectManipulator field : content.getExecutionContent().getSimulatedObjectAccessVector())
		{
			try
			{
				if (field.getField().getType().equals(Double.class) || field.getField().getType().equals(double.class))
				{
					field.updateObject(0.0, field.getChangeParent());
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void storeChangeValues(boolean pre_jump)
	{
		for (DynamicObjectManipulator field : content.getExecutionContent().getFieldParentMap().values())
		{
			try
			{

				if (pre_jump)
				{
					field.updateObject(field.getObject(), field.getChangeParent());
				} else
				{

					field.updateObject(field.getChange());
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
