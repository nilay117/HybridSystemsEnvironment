package edu.ucsc.cross.hse.core.operator;

import com.be3short.obj.access.FieldFinder;
import com.be3short.obj.manipulation.DynamicObjectManipulator;
import com.be3short.obj.manipulation.FieldMapper;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import edu.ucsc.cross.hse.core.object.HybridSystem.HybridSystemOperator;
import edu.ucsc.cross.hse.core.object.Objects;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class ObjectOperator
{

	public static final FieldMapper fieldMapper = new FieldMapper(Objects.class);
	public static HashMap<String, ArrayList<Field>> fields = new HashMap<String, ArrayList<Field>>();
	private HybridTime simTime; // Simulation time
	private HashMap<Object, DynamicObjectManipulator> objectMap;
	private DynamicObjectManipulator[] objectAccessVector;
	private double[] valueVector;
	private double[] changeVector;
	ExecutionOperator manager;

	public ObjectOperator(ExecutionOperator manager)
	{
		this.manager = manager;
		simTime = new HybridTime(0.0, 0);
	}

	///////// Access Functions

	public HybridTime getHybridSimTime()
	{
		return simTime;
	}

	public Double getSimulationTime()
	{
		return simTime.getTime();
	}

	public void setSimTime(HybridTime simTime)
	{
		this.simTime = simTime;
	}

	public HashMap<Object, DynamicObjectManipulator> getFieldParentMap()
	{
		return objectMap;
	}

	public void setFieldParentMap(HashMap<Object, DynamicObjectManipulator> fieldParentMap)
	{
		this.objectMap = fieldParentMap;
	}

	public DynamicObjectManipulator[] getSimulatedObjectAccessVector()
	{
		return objectAccessVector;
	}

	public void setSimulatedObjectAccessVector(DynamicObjectManipulator[] simulatedObjectAccessVector)
	{
		this.objectAccessVector = simulatedObjectAccessVector;
	}

	public double[] getSimulatedObjectValueVector()
	{
		return valueVector;
	}

	public void setSimulatedObjectValueVector(double[] simulatedObjectValueVector)
	{
		this.valueVector = simulatedObjectValueVector;
	}

	public double[] getValueVector()
	{

		return valueVector;
	}

	public double[] getChangeVector()
	{
		return changeVector;
	}

	public void setChangeVector(double[] changeVector)
	{
		this.changeVector = changeVector;
	}

	///////// Time Operator Functions

	public void updateSimulationTime(HybridTime simTime)
	{
		setSimTime(simTime);
	}

	public void updateSimulationTime(Double simTime)
	{
		setSimTime(new HybridTime(simTime, getHybridSimTime().getJumps()));
	}

	public void updateSimulationTime(Double simTime, Integer jump_increment)

	{
		setSimTime(new HybridTime(simTime, getHybridSimTime().getJumps() + jump_increment));
	}

	public void incrementJumpIndex(Integer jump_increment)
	{
		setSimTime(new HybridTime(getHybridSimTime().getTime(), getHybridSimTime().getJumps() + jump_increment));
	}

	///////// Vector Operator Functions

	public void readStateValues(double y[])
	{
		for (int i = 0; i < y.length; i++)
		{
			getSimulatedObjectAccessVector()[i].updateObject(y[i]);
		}
	}

	public double[] updateValueVector(double y[])
	{

		for (int i = 0; i < valueVector.length; i++)
		{
			valueVector[i] = (double) getSimulatedObjectAccessVector()[i].getObject();
			if (y != null)
			{

				y[i] = (double) getSimulatedObjectAccessVector()[i].getObject();

			}
			DynamicObjectManipulator.isNumericalValue(getSimulatedObjectAccessVector()[i], valueVector[i]);
		}

		return valueVector;
	}

	public double[] updateChangeVector(double yDot[])
	{

		for (int i = 0; i < changeVector.length; i++)
		{
			changeVector[i] = (double) getSimulatedObjectAccessVector()[i].getChange();
			if (yDot != null)
			{
				yDot[i] = (double) getSimulatedObjectAccessVector()[i].getChange();
			}
		}

		return changeVector;
	}

	///////// Setup Functions

	public void prepareComponents(ExecutionOperator manager)
	{
		simTime = new HybridTime(0.0, 0);
		initializeFieldMapper();
		objectMap = initializeObjectAccessMap(manager);
		objectAccessVector = initializeObjectAccessVector(objectMap, manager);
		valueVector = initializeValueVector(objectAccessVector, false);
		changeVector = initializeValueVector(objectAccessVector, true);
	}

	private void initializeFieldMapper()
	{
		HashMap<Class<?>, String> classes = new HashMap<Class<?>, String>();
		HashMap<String, ArrayList<Field>> elems = new HashMap<String, ArrayList<Field>>();
		HashMap<String, ArrayList<Field>> ele = new HashMap<String, ArrayList<Field>>();
		for (HybridSystem<?> sys : manager.getContents().getSystems())
		{
			if (!classes.containsValue(sys.getState().getClass()))
			{
				classes.put(sys.getState().getClass(), sys.getState().getClass().getName());
				elems.put(sys.getState().getClass().getName(), new ArrayList<Field>());
			}
		}

		getClassFieldMapping(elems, classes.keySet(), Objects.class);
		while (fields(ele) < fields(elems))
		{
			getClassFieldMapping(elems, classes.keySet(), Objects.class);
			ele = elems;
		}
		fields = elems;
	}

	public void getClassFieldMapping(HashMap<String, ArrayList<Field>> elements, Set<Class<?>> search_classes,
	Class<?> search_class)
	{

		for (Class<?> clas : search_classes)
		{
			for (Field fields : clas.getDeclaredFields())
			{
				if (!fields.getName().contains("$SWITCH_TABLE$") && !fields.getType().equals(search_class))// &&
																											// !skipFields.contains(fields))
				{
					if (!elements.get(clas.getName()).contains(fields))
					{
						elements.get(clas.getName()).add(fields);
					}
				}
			}
			if (search_classes.contains(clas.getSuperclass()))
			{
				Class<?> superClass = clas.getSuperclass();
				ArrayList<Field> extendedFields = new ArrayList<Field>(Arrays.asList(superClass.getDeclaredFields()));
				while (search_classes.contains(superClass))
				{
					for (Field fi : extendedFields)
					{
						if (!fi.getName().contains("$SWITCH_TABLE$") && !fi.getType().equals(search_class))// &&
						{
							if (!elements.get(clas.getName()).contains(fi))
							{
								elements.get(clas.getName()).add(fi);
							}
						}
					}
					clas = superClass;
					superClass = superClass.getSuperclass();
					extendedFields.addAll(Arrays.asList(superClass.getDeclaredFields()));

				}
				// elements.put(clas, extendedFields.toArray(new Field[extendedFields.size()]));
			}

		}

	}

	private static Integer fields(HashMap<String, ArrayList<Field>> test)
	{
		Integer count = 0;
		for (ArrayList<Field> clas : test.values())
		{
			count += clas.size();
		}
		return count;
	}

	private HashMap<Object, DynamicObjectManipulator> initializeObjectAccessMap(ExecutionOperator manager)
	{
		// initializeFieldMapper();
		HashMap<Object, DynamicObjectManipulator> newObjectAccessMap = new HashMap<Object, DynamicObjectManipulator>();
		for (HybridSystem<?> system : manager.getContents().getSystems())
		{

			try
			{
				HybridSystemOperator.initializeDynamicState(system);

				initializeMap(newObjectAccessMap, system.getState(), HybridSystemOperator.getDynamicState(system));

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return newObjectAccessMap;
	}

	@SuppressWarnings("unchecked")
	private void initializeMap(HashMap<Object, DynamicObjectManipulator> object_map, Object state, Object dynamic)
	{

		for (Field field : fieldMapper.getClassFields(state.getClass()))// fields.get(state.getClass().getName()))
		{
			try
			{
				field.setAccessible(true);

				// if (fields.containsKey(field.getType()))
				if (fieldMapper.isState(field.getType()))
				{
					initializeMap(object_map, field.get(state), field.get(dynamic));
				} else
				{
					if (field.getType().equals(ArrayList.class))

					{
						ArrayList<Object> states = (ArrayList<Object>) field.get(state);
						ArrayList<Object> dynamics = (ArrayList<Object>) field.get(dynamic);
						if (!states.isEmpty())
						{
							if (FieldFinder.containsSuper(states.get(0), Objects.class))
							{
								for (int i = 0; i < states.size(); i++)
								{
									initializeMap(object_map, states.get(i), dynamics.get(i));
								}
							}
						}
					}

					else if (!object_map.containsKey(state.toString() + field.getName()))
					{

						try
						{
							DynamicObjectManipulator objAccess = new DynamicObjectManipulator(field, state, dynamic);
							object_map.put(state.toString() + field.getName(), objAccess);
						} catch (Exception badField)
						{
							badField.printStackTrace();
						}
					}
				}
			} catch (Exception e)
			{

			}
		}

		// initializeMap(object_map, state, dynamic);
	}

	private static DynamicObjectManipulator[] initializeObjectAccessVector(
	HashMap<Object, DynamicObjectManipulator> object_map, ExecutionOperator manager)
	{
		ArrayList<DynamicObjectManipulator> stateObjectList = new ArrayList<DynamicObjectManipulator>();
		for (DynamicObjectManipulator obj : object_map.values())
		{
			if (obj.getField().getType().equals(Double.class) || obj.getField().getType().equals(double.class))
			{
				stateObjectList.add(obj);
			} else
			{
				try
				{
					@SuppressWarnings("unused")
					Double test = (Double) obj.getObject();
					stateObjectList.add(obj);
				} catch (Exception notDouble)
				{

				}
			}
		}
		DynamicObjectManipulator[] newObjectAccessVector = stateObjectList
		.toArray(new DynamicObjectManipulator[stateObjectList.size()]);
		return newObjectAccessVector;
	}

	private static double[] initializeValueVector(DynamicObjectManipulator[] access, boolean zero_values)
	{
		double[] valueVector = new double[access.length];
		for (int i = 0; i < access.length; i++)
		{
			try
			{
				if (zero_values)
				{
					valueVector[i] = 0.0;

				} else
				{
					valueVector[i] = (double) access[i].getObject();
				}
			} catch (Exception e)
			{
				valueVector[i] = 0.0;
				e.printStackTrace();
			}
		}
		return valueVector;
	}
}