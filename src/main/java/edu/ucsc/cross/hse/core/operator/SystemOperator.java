package edu.ucsc.cross.hse.core.operator;

import com.be3short.io.xml.XMLParser;
import com.be3short.obj.manipulation.DynamicObjectManipulator;
import edu.ucsc.cross.hse.core.io.Console;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import edu.ucsc.cross.hse.core.object.HybridSystem.HybridSystemOperator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SystemOperator
{

	final int NUM_THREADS = Runtime.getRuntime().availableProcessors() - 1;
	// System.out.println(NUM_THREADS);
	final ExecutorService executor = Executors.newWorkStealingPool(NUM_THREADS);
	int activeThreads = 0;
	private HashMap<Integer, ArrayList<HybridSystem<?>>> systemz;
	private Thread[] tt;
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

		jo = jump_occurring;
		prepareThreads();
		for (int i = 0; i < tt.length; i++)
		{
			executor.execute(tt[i]);
		}
		Future<Boolean> done = null;
		for (int i = 0; i < NUM_THREADS; i++)
		{
			done = executor.submit(finished());
		}
		try
		{
			done.get();
		} catch (InterruptedException | ExecutionException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// executor.
		// while (!executor.isTerminated())
		// {
		//
		// }
		// executor.shutdown();
		// try
		// {
		// executor.awaitTermination(10000, TimeUnit.MILLISECONDS);
		// } catch (InterruptedException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// while (!executor.isTerminated())
		// {
		//
		// }
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

	private void prepareThreads()
	{
		if (tt == null)
		{
			tt = new Thread[content.getContents().getSystems().size()];
			for (int i = 0; i < content.getContents().getSystems().size(); i++)
			{
				tt[i] = new Thread(prepareThread(content.getContents().getSystems().get(i)));
			}
		}
	}

	private void prepareThreadz()
	{
		if (tt == null)
		{
			tt = new Thread[Runtime.getRuntime().availableProcessors() - 1];
			int start = 0;
			int end = 1 + content.getContents().getSystems().size() / (Runtime.getRuntime().availableProcessors() - 1);

			for (int i = 0; i < Runtime.getRuntime().availableProcessors() - 1; i++)
			{

				int endVal = (i + 1) * end;
				if (endVal >= content.getContents().getSystems().size())
				{
					endVal = content.getContents().getSystems().size() - 1;
				}
				tt[i] = new Thread(prepareThreadz(i * end, endVal));
			}
		}
	}

	private Callable<Boolean> finished()
	{
		return new Callable<Boolean>()
		{

			public Boolean call()
			{

				return true;
			}
		};

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

	private Runnable prepareThreadz(Integer start_index, Integer end)
	{
		return new Runnable()
		{

			public void run()
			{
				for (int i = start_index; i <= end; i++)
				{
					HybridSystem<?> hs = content.getContents().getSystems().get(i);
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
