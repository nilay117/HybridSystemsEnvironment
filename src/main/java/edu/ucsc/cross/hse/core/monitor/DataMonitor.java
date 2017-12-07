package edu.ucsc.cross.hse.core.monitor;

import com.be3short.obj.manipulation.DynamicObjectManipulator;
import com.be3short.obj.manipulation.ObjectManipulator;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.data.HybridArc;
import edu.ucsc.cross.hse.core.object.ObjectSet;
import edu.ucsc.cross.hse.core.object.ObjectSet.ObjectSetAPI;
import edu.ucsc.cross.hse.core.operator.ExecutionOperator;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.util.ArrayList;
import java.util.HashMap;

public class DataMonitor
{

	private ExecutionOperator manager;

	/*
	 * This mapping contains the initial value of every object contained within the environment
	 */
	private HashMap<Object, Object> initialValueMapping;
	private Double initialValueTime;
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
		// if (time > initialValueTime && time > 0)
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
						// if (lastTime() > 0.0)
						{
							removeLastValue();
						}
					} catch (Exception removeLastValueFail)
					{
						removeLastValueFail.printStackTrace();
					}
				}
			} // else
				// {
				// System.out.println("yo");
				// }
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

	public void revertToLastStoredValue(double initial_val_time)
	{

		removePreviousVals(lastTime());
		manager.getExecutionContent().readStateValues(manager.getExecutionContent().updateValueVector(null));
		manager.getExecutionContent().updateValueVector(null);
		manager.getExecutionContent().updateSimulationTime(lastTime(),
		manager.getDataCollector().getLastStoredTime().getJumps());
		storeNewData(lastTime());

	}

	public void storeNewData(Double time)
	{
		// removePreviousVals(time);
		try
		{
			// if (!time.equals(manager.getDataCollector().getStoreTimes().get(0).getTime()))
			// {
			for (DataSeries<?> data : manager.getDataCollector().getAllDataSeries())
			// .getSimulatedObjectAccessVector().length; objIndex++)
			{
				ObjectManipulator obj = manager.getExecutionContent().getFieldParentMap()
				.get(data.getParent().toString() + data.getChild().getName());// [objIndex];
				// DataSeries<?> data = manager.getDataCollector().getGlobalStateData().get(objIndex);
				storeDataGeneral(data, obj.getObject());
			}
			// }
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
		if (jump_status.equals(JumpStatus.JUMP_OCCURRED) || jump_status.equals(JumpStatus.MULTI_JUMP_OCCURRED))
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
			for (Integer objIndex = 1; objIndex < manager.getDataCollector().getAllDataSeries().size(); objIndex++)
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
		// System.out.println(time + " last " + lastTime());
		if (jump_status.equals(JumpStatus.MULTI_JUMP_OCCURRED) || jump_status.equals(JumpStatus.JUMP_OCCURRED))
		{
			storeNewData(time);
		} else if (jump_status.equals(JumpStatus.JUMP_DETECTED))
		// || jump_status.equals(JumpStatus.JUMP_OCCURRED))
		{
			// removePreviousVals(time);
			// removePreviousVals(time);
			System.out.println(time + " last " + lastTime());
			// if (time >= (lastTime() + manager.getSettings().getOutputSettings().dataPointInterval))
			{
				storeNewData(time);
			}
		} else if (jump_status.equals(JumpStatus.APPROACHING_JUMP))
		{

			if (time >= (lastTime() + manager.getSettings().getOutputSettings().dataPointInterval))
			{
				removePreviousVals(time);
				storeNewData(time);
			}
		} else
		{
			//
			// if (time > (lastTime() + manager.getSettings().getOutputSettings().dataPointInterval))
			// {
			//
			storeNewData(time);
			// }
			//
		}

	}

	private void updateTime(Double time, JumpStatus jump_status)
	{
		if (jump_status.equals(JumpStatus.JUMP_OCCURRED) || jump_status.equals(JumpStatus.MULTI_JUMP_OCCURRED))
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

	public void storeInitialValues(double initial_val_time)
	{
		initialValueTime = initial_val_time;
		initialValueMapping = new HashMap<Object, Object>();
		HashMap<Object, DynamicObjectManipulator> objectManipulators = manager.getExecutionContent()
		.getFieldParentMap();

		for (Object obj : manager.getExecutionContent().getFieldParentMap().keySet())
		{
			initialValueMapping.put(obj, objectManipulators.get(obj).getObject());
		}
	}

	public void revertToInitialValues()
	{
		revertToInitialValues(null);
	}

	public void revertToInitialValues(Double initial_val_time)
	{
		Double initialTime = initial_val_time;
		if (initialTime == null)
		{
			initialTime = initialValueTime;
		}
		manager.getDataCollector().getStoreTimes().clear();
		// restoreInitialData(true);
		// initialValueMapping = new HashMap<Object, Object>();
		HashMap<Object, DynamicObjectManipulator> objectManipulators = manager.getExecutionContent()
		.getFieldParentMap();
		// for (DataSeries<?> data : manager.getDataCollector().getAllDataSeries())
		// // .getSimulatedObjectAccessVector().length; objIndex++)
		// {
		// data.getAllStoredData().clear();
		// }
		for (Object obj : manager.getExecutionContent().getFieldParentMap().keySet())
		{
			try
			{
				objectManipulators.get(obj).updateObject(initialValueMapping.get(obj));
				// objectManipulators.get(obj).getField().set(objectManipulators.get(obj).getChangeParent(), 0.0);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		manager.getExecutionContent().readStateValues(manager.getExecutionContent().updateValueVector(null));
		manager.getExecutionContent().updateValueVector(null);
		manager.getExecutionContent().updateSimulationTime(initialTime, 0);
		// manager.getExecutionContent().updateValueVector(null);
		// manager.getExecutionContent().updateSimulationTime(initialTime);
		storeNewData(initialTime);
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
