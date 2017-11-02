package edu.ucsc.cross.hse.core.monitor;

import com.be3short.obj.manipulation.ObjectManipulator;
import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.object.Objects;
import edu.ucsc.cross.hse.core.operator.ExecutionOperator;
import edu.ucsc.cross.hse.core.time.HybridTime;

@Loggable(Loggable.TRACE)
public class DataMonitor
{

	private ExecutionOperator manager;
	// private Double nextStoreTime;

	public DataMonitor(ExecutionOperator manager)
	{
		this.manager = manager;
		// nextStoreTime = 0.0;
	}

	public void gatherData(double time, double state_vector[], JumpStatus jump_status)
	{
		gatherData(time, state_vector, jump_status, false);
	}

	public void gatherData(double time, double state_vector[], JumpStatus jump_status, boolean override_store)
	{

		updateTime(time, jump_status);
		loadData(state_vector, jump_status);
		if (override_store)
		{
			storeNewData(time);

		} else
		{
			updateData(time, jump_status);
		}
	}

	private void loadData(double state_vector[], JumpStatus jump_status)
	{
		if (jump_status.equals(JumpStatus.JUMP_OCCURRED))
		{
			manager.getExecutionContent().updateValueVector(state_vector);
		} else
		{
			manager.getExecutionContent().readStateValues(state_vector);
		}
	}

	private void updateData(Double time, JumpStatus jump_status)
	{
		removePreviousVals(time, false);
		if (jump_status.equals(JumpStatus.JUMP_DETECTED) || jump_status.equals(JumpStatus.JUMP_OCCURRED))
		{
			storeNewData(time);
		} else
		{
			if (time > (lastTime() + manager.getSettings().getOutputSettings().dataPointInterval))
			{
				storeNewData(time);
			}

		}
	}

	private void updateTime(Double time, JumpStatus jump_status)
	{
		if (jump_status.equals(JumpStatus.JUMP_OCCURRED))
		{
			manager.getExecutionContent().updateSimulationTime(
			new HybridTime(time, manager.getExecutionContent().getHybridSimTime().getJumps() + 1));
		} else
		{
			manager.getExecutionContent()
			.updateSimulationTime(new HybridTime(time, manager.getExecutionContent().getHybridSimTime().getJumps()));
		}
	}

	public void storeNewData(Double time)
	{
		// removePreviousVals(time);
		for (Integer objIndex = 0; objIndex < manager.getExecutionContent()
		.getSimulatedObjectAccessVector().length; objIndex++)
		{
			ObjectManipulator obj = manager.getExecutionContent().getSimulatedObjectAccessVector()[objIndex];
			DataSeries<Double> data = manager.getDataCollector().getGlobalStateData().get(objIndex);
			data.storeData((Double) obj.getObject());
		}
		manager.getDataCollector().getStoreTimes()
		.add(new HybridTime(time, manager.getExecutionContent().getHybridSimTime().getJumps()));
	}

	public double[] getLastValueArray()
	{
		double[] values = new double[manager.getSimEngine().getDimension()];
		int i = 0;
		for (DataSeries<Double> series : manager.getDataCollector().getGlobalStateData())
		{

			values[i++] = series.getAllStoredData().get(series.getAllStoredData().size() - 1);
		}
		return values;
	}

	public void removePreviousVals(Double time, boolean err)
	{
		try
		{
			while (((lastTime() >= time) && err) || ((lastTime() > time) && !err))
			{
				try
				{
					removeLastValue();
				} catch (Exception removeLastValueFail)
				{
				}
			}
			if (err)
			{
				revertToLastStoredValue();
			}

		} catch (Exception removeValsFail)
		{
		}
	}

	private void revertToLastStoredValue()
	{
		manager.getExecutionContent().readStateValues(getLastValueArray());
		manager.getExecutionContent().updateValueVector(null);
		manager.getExecutionContent().updateSimulationTime(lastTime());
		storeNewData(lastTime());
	}

	private void removeLastValue()
	{
		int i = manager.getDataCollector().getStoreTimes().indexOf(manager.getDataCollector().getLastStoredTime());

		if (manager.getDataCollector().getStoreTimes().size() > i)
		{
			for (Integer objIndex = 0; objIndex < manager.getExecutionContent()
			.getSimulatedObjectAccessVector().length; objIndex++)
			{
				DataSeries<Double> data = manager.getDataCollector().getGlobalStateData().get(objIndex);
				if (data.getAllStoredData().size() > i)
				{
					data.getAllStoredData().remove(i);
				}
			}
			manager.getDataCollector().getStoreTimes().remove(i);
		}

	}

	private Double lastTime()
	{
		return manager.getDataCollector().getLastStoredTime().getTime();
	}

	public void restoreInitialData()
	{
		manager.getDataCollector().getStoreTimes().clear();
		for (Integer objIndex = 0; objIndex < manager.getExecutionContent()
		.getSimulatedObjectAccessVector().length; objIndex++)
		{
			ObjectManipulator obj = manager.getExecutionContent().getSimulatedObjectAccessVector()[objIndex];
			DataSeries<Double> data = manager.getDataCollector().getGlobalStateData().get(objIndex);
			obj.updateObject(data.getAllStoredData().get(0));
			data.getAllStoredData().clear();
		}
	}

	public void loadMap()
	{

		manager.getDataCollector().getGlobalStateData().clear();
		for (ObjectManipulator state : manager.getExecutionContent().getSimulatedObjectAccessVector())
		{
			String parentName = state.getParent().getClass().getSimpleName();
			try
			{
				Objects parent = (Objects) state.getParent();
				parentName = parent.info().getName();
			} catch (Exception e)
			{

			}
			manager.getDataCollector().getGlobalStateData()
			.add(new DataSeries<Double>(manager.getDataCollector().getStoreTimes(), state.getField().getName(),
			parentName, state.getParent().toString()));
		}
	}
}
