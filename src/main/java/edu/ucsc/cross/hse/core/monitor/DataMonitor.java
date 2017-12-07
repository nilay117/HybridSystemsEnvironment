package edu.ucsc.cross.hse.core.monitor;

import java.util.ArrayList;

import com.be3short.obj.manipulation.ObjectManipulator;

import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.data.HybridArc;
import edu.ucsc.cross.hse.core.object.ObjectSet;
import edu.ucsc.cross.hse.core.object.ObjectSet.ObjectSetAPI;
import edu.ucsc.cross.hse.core.operator.ExecutionOperator;
import edu.ucsc.cross.hse.core.time.HybridTime;

public class DataMonitor
{

	private ExecutionOperator manager;
	// private Double nextStoreTime;

	public void loadMap()
	{

		manager.getDataCollector().getHybridArcMap().clear();
		for (ObjectManipulator state : manager.getExecutionContent().getFieldParentMap().values())// .getSimulatedObjectAccessVector())
		{
			// state.getParent()
			// String parentName = state.getParent().getClass().getSimpleName();
			try
			{
				ObjectSet parent = (ObjectSet) state.getParent();
				if (ObjectSetAPI.isHistorySaved(parent))
				{
					if (!manager.getDataCollector().getHybridArcMap().containsKey(parent))
					{
						manager.getDataCollector().getHybridArcMap().put(parent,
						HybridArc.createArc(parent, manager.getDataCollector().getStoreTimes()));
					}
					HybridArc<?> solution = manager.getDataCollector().getHybridArcMap().get(parent);
					solution.addSeries(state.getField(), DataSeries.getSeries(
					manager.getDataCollector().getStoreTimes(), parent, state.getField(), state.getField().getType()));
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		}
		initializeUniqueNamesAndAddresses();
		EnvironmentData.populateListMap(manager.getDataCollector());
	}

	public void initializeUniqueNamesAndAddresses()
	{
		ArrayList<ObjectSet> duplicateNames = new ArrayList<ObjectSet>();
		for (ObjectManipulator state : manager.getExecutionContent().getFieldParentMap().values())// .getSimulatedObjectAccessVector())
		{
			try
			{
				ObjectSet parent = (ObjectSet) state.getParent();
				if (!duplicateNames.contains(parent))
				{
					int dup = 1;
					for (ObjectSet set : duplicateNames)
					{
						if (parent.data().getName().equals(set.data().getName()))
						{
							dup++;
						}
					}
					parent.data().setUniqueLabel(parent.data().getName() + "(" + dup + ")");
					duplicateNames.add(parent);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		}
		removeIndicesFromNonDuplicateNames(duplicateNames);
	}

	/*
	 * Removes the index number appended to a unique name that was already unique.
	 */
	private void removeIndicesFromNonDuplicateNames(ArrayList<ObjectSet> objects)
	{
		for (ObjectSet obj : objects)
		{
			boolean nameAlreadyUnique = true;
			for (ObjectSet otherObj : objects)
			{
				if (!otherObj.equals(obj))
				{
					if (otherObj.data().getName().equals(obj.data().getName()))
					{
						nameAlreadyUnique = false;

					}
				}
			}
			if (nameAlreadyUnique)
			{
				obj.data().setUniqueLabel(obj.data().getName());
			}

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
			} else
			{
				System.out.println("yo");
			}
		} catch (Exception removeValsFail)
		{
			removeValsFail.printStackTrace();
		}
	}

	public void restoreInitialData()
	{
		restoreInitialData(true);
	}

	public void restoreInitialData(boolean clear)
	{
		manager.getDataCollector().getStoreTimes().clear();
		for (Integer objIndex = 0; objIndex < manager.getExecutionContent()
		.getSimulatedObjectAccessVector().length; objIndex++)
		{
			ObjectManipulator obj = manager.getExecutionContent().getSimulatedObjectAccessVector()[objIndex];
			DataSeries<?> data = manager.getDataCollector().getAllDataSeries().get(objIndex);
			obj.updateObject(data.getAllStoredData().get(0));
			if (clear)
			{
				data.getAllStoredData().clear();
			}
		}
	}

	public void revertToLastStoredValue(Double time)
	{
		if (manager.getDataCollector().getStoreTimes().size() > 1)
		{
			removePreviousVals(time);
			manager.getExecutionContent().readStateValues(manager.getExecutionContent().updateValueVector(null));
			manager.getExecutionContent().updateValueVector(null);
			manager.getExecutionContent().updateSimulationTime(lastTime());
			storeNewData(lastTime());
		} else
		{
			restoreInitialData(false);
			manager.getExecutionContent().updateSimulationTime(manager.getDataCollector().getStoreTimes().get(0));
			// manager.getExecutionContent().updateValueVector(null);
		}
	}

	public void storeNewData(Double time)
	{
		// removePreviousVals(time);
		try
		{
			if (!time.equals(manager.getDataCollector().getStoreTimes().get(0).getTime()))
			{
				for (DataSeries<?> data : manager.getDataCollector().getAllDataSeries())
				// .getSimulatedObjectAccessVector().length; objIndex++)
				{
					ObjectManipulator obj = manager.getExecutionContent().getFieldParentMap()
					.get(data.getParent().toString() + data.getChild().getName());// [objIndex];
					// DataSeries<?> data = manager.getDataCollector().getGlobalStateData().get(objIndex);
					storeDataGeneral(data, obj.getObject());
				}
			}
		} catch (

		Exception removeValsFail)
		{
			removeValsFail.printStackTrace();
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
			for (Integer objIndex = 0; objIndex < manager.getDataCollector().getAllDataSeries().size(); objIndex++)
			{
				DataSeries<?> data = manager.getDataCollector().getAllDataSeries().get(objIndex);
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
