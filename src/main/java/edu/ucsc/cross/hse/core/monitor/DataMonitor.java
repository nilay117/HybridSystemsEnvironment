package edu.ucsc.cross.hse.core.monitor;

import com.be3short.obj.manipulation.ObjectManipulator;
import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.object.ObjectSet;
import edu.ucsc.cross.hse.core.operator.ExecutionOperator;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.util.ArrayList;

public class DataMonitor
{

	private ExecutionOperator manager;
	// private Double nextStoreTime;

	public void loadMap()
	{

		manager.getDataCollector().getGlobalStateData().clear();
		for (ObjectManipulator state : manager.getExecutionContent().getSimulatedObjectAccessVector())
		{
			String parentName = state.getParent().getClass().getSimpleName();
			try
			{
				ObjectSet parent = (ObjectSet) state.getParent();
				parentName = parent.extension().getName();
			} catch (Exception e)
			{

			}
			manager.getDataCollector().getGlobalStateData()
			.add(DataSeries.getSeries(manager.getDataCollector().getStoreTimes(), state.getObject().getClass(),
			state.getField().getName(), parentName, state.getParent().toString()));
		}
	}

	public void performDataActions(double time, double state_vector[], JumpStatus jump_status)
	{
		performDataActions(time, state_vector, jump_status, false);
	}

	public void performDataActions(double time, double state_vector[], JumpStatus jump_status, boolean override_store)
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

	public void removePreviousVals(Double time)
	{
		try
		{
			if (lastTime() > 0.0)
			{
				while (lastTime() >= time)
				{
					try
					{
						removeLastValue();
					} catch (Exception removeLastValueFail)
					{
						removeLastValueFail.printStackTrace();
					}
				}
			}

		} catch (Exception removeValsFail)
		{
			removeValsFail.printStackTrace();
		}
	}

	public void restoreInitialData()
	{
		manager.getDataCollector().getStoreTimes().clear();
		for (Integer objIndex = 0; objIndex < manager.getExecutionContent()
		.getSimulatedObjectAccessVector().length; objIndex++)
		{
			ObjectManipulator obj = manager.getExecutionContent().getSimulatedObjectAccessVector()[objIndex];
			DataSeries<?> data = manager.getDataCollector().getGlobalStateData().get(objIndex);
			obj.updateObject(data.getAllStoredData().get(0));
			data.getAllStoredData().clear();
		}
	}

	public void revertToLastStoredValue(Double time)
	{
		removePreviousVals(time);
		manager.getExecutionContent().readStateValues(manager.getExecutionContent().updateValueVector(null));
		manager.getExecutionContent().updateValueVector(null);
		manager.getExecutionContent().updateSimulationTime(lastTime());
		storeNewData(lastTime());
	}

	public void storeNewData(Double time)
	{
		// removePreviousVals(time);
		for (Integer objIndex = 0; objIndex < manager.getExecutionContent()
		.getSimulatedObjectAccessVector().length; objIndex++)
		{
			ObjectManipulator obj = manager.getExecutionContent().getSimulatedObjectAccessVector()[objIndex];
			DataSeries<?> data = manager.getDataCollector().getGlobalStateData().get(objIndex);
			storeDataGeneral(data, obj.getObject());
		}
		manager.getDataCollector().getStoreTimes()
		.add(new HybridTime(time, manager.getExecutionContent().getHybridSimTime().getJumps()));
	}

	private Double lastTime()
	{
		return manager.getDataCollector().getLastStoredTime().getTime();
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

	private void removeLastValue()
	{
		int i = manager.getDataCollector().getStoreTimes().indexOf(manager.getDataCollector().getLastStoredTime());

		if (manager.getDataCollector().getStoreTimes().size() > i)
		{
			for (Integer objIndex = 0; objIndex < manager.getExecutionContent()
			.getSimulatedObjectAccessVector().length; objIndex++)
			{
				DataSeries<?> data = manager.getDataCollector().getGlobalStateData().get(objIndex);
				if (data.getAllStoredData().size() > i)
				{
					data.getAllStoredData().remove(i);
				}
			}
			manager.getDataCollector().getStoreTimes().remove(i);
		}

	}

	private void updateData(Double time, JumpStatus jump_status)
	{

		if (jump_status.equals(JumpStatus.JUMP_OCCURRED))
		{
			storeNewData(time);
		} else if (jump_status.equals(JumpStatus.JUMP_DETECTED))
		{
			removePreviousVals(time);
			storeNewData(time);
		} else // if (jump_status.equals(JumpStatus.APPROACHING_JUMP) || )
		{
			if (time > (lastTime() + manager.getSettings().getOutputSettings().dataPointInterval))
			{
				removePreviousVals(time);
				storeNewData(time);
			}

		}

	}

	private void updateTime(Double time, JumpStatus jump_status)
	{
		if (jump_status.equals(JumpStatus.JUMP_OCCURRED))
		{
			manager.getExecutionContent().updateSimulationTime(time, 1);

		} else
		{
			manager.getExecutionContent().updateSimulationTime(time);
		}
	}

	public DataMonitor(ExecutionOperator manager)
	{
		this.manager = manager;
		// nextStoreTime = 0.0;
	}

	/*
	 * Store the current value of the data element
	 */
	public static <X> void storeData(DataSeries<X> data, X value)
	{

		data.getAllStoredData().add(value);
	}

	public static <T extends Object> void storeDataGeneral(ArrayList<T> allStoredData, Object parseString)
	{
		// TODO Auto-generated method stub
		allStoredData.add((T) parseString);
	}

	/*
	 * Store the current value of the data element
	 */
	@SuppressWarnings("unchecked")
	public static <X> void storeDataGeneral(DataSeries<X> data, Object value)
	{

		data.getAllStoredData().add((X) value);
	}

}
