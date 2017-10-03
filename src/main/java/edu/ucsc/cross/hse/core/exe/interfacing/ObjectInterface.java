package edu.ucsc.cross.hse.core.exe.interfacing;

import bs.commons.objects.access.FieldFinder;
import com.be3short.data.file.xml.XMLParser;
import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.exe.access.ObjectManipulator;
import edu.ucsc.cross.hse.core.exe.access.StateFieldMapper;
import edu.ucsc.cross.hse.core.exe.operator.EnvironmentManager;
import edu.ucsc.cross.hse.core.obj.structure.HybridSystem;
import edu.ucsc.cross.hse.core.obj.structure.State;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * Allows objects to be manipulated without disrupting pointers.
 */

@Loggable(Loggable.TRACE)
public class ObjectInterface
{

	private EnvironmentManager content;
	private HashMap<Integer, ObjectManipulator> numericalStateMap;
	private HashMap<Object, ObjectManipulator> fieldParentMap;

	public ObjectInterface(EnvironmentManager content)
	{
		this.content = content;
	}

	public void prepareComponents()
	{
		loadFieldParentMap();
		initializeNumericalStateMap();
	}

	private void loadFieldParentMap()
	{
		{
			fieldParentMap = new HashMap<Object, ObjectManipulator>();
			for (HybridSystem<?> system : content.getContents().getSystems())
			{

				HybridSystem.initializeDynamicState(system);

				initializeMap(system.getState(), HybridSystem.getDynamicState(system));

			}
		}
	}

	public void initializeMap(Object state, Object dynamic)
	{
		System.out.println(state.toString());
		for (Field field : StateFieldMapper.getClassFields(state.getClass()))
		{
			try
			{
				field.setAccessible(true);
				{
					if (StateFieldMapper.isState(field.getType()))
					{
						initializeMap(field.get(state), field.get(dynamic));
					} else
					{
						if (field.getType().equals(ArrayList.class))

						{
							ArrayList<Object> states = (ArrayList<Object>) field.get(state);
							ArrayList<Object> dynamics = (ArrayList<Object>) field.get(dynamic);
							if (!states.isEmpty())
							{
								if (FieldFinder.containsSuper(states.get(0), State.class))
								{
									for (int i = 0; i < states.size(); i++)
									{
										initializeMap(states.get(i), dynamics.get(i));
									}
								}
							}
						}

						else if (!fieldParentMap.containsKey(state.toString() + field.getName()))
						{

							try
							{
								ObjectManipulator objAccess = new ObjectManipulator(field, state, dynamic);
								fieldParentMap.put(state.toString() + field.getName(), objAccess);
								// fieldParentMap.put(dynamic.toString() + field.getName(), objAccess);
							} catch (Exception badField)
							{
								badField.printStackTrace();
							}
						}
					}
				}
			} catch (Exception e)// | IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void initializeNumericalStateMap()
	{
		// if (numericalStateMap == null)
		{
			numericalStateMap = new HashMap<Integer, ObjectManipulator>();
			for (ObjectManipulator obj : fieldParentMap.values())
			{
				if (obj.getField().getType().equals(Double.class) || obj.getField().getType().equals(double.class))
				{
					numericalStateMap.put(numericalStateMap.size(), obj);
				}
			}
		}

	}

	public HashMap<Integer, ObjectManipulator> getNumericalStateMap()
	{
		return numericalStateMap;
	}

	public HashMap<Object, ObjectManipulator> getFieldParentMap()
	{
		return fieldParentMap;
	}

}
