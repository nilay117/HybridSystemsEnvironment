package edu.ucsc.cross.hybrid.env.core.structure;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.access.ObjectFinder;
import bs.commons.objects.execution.Initializer;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hybrid.env.core.components.HybridSystem;

public abstract class Component implements Initializer//implements ComponentInterface
{

	private static Class[] deeperSearchClasses = loadDeeperSearchClasses();

	private static Class[] loadDeeperSearchClasses()
	{
		Class[] deeper = new Class[]
		{ Component.class, HashMap.class, ArrayList.class };
		ObjectFinder.setDeeperSearchClasses(deeper);
		return deeper;
	}

	private Boolean initialized;

	public Boolean isInitialized()
	{
		if (initialized == null)
		{
			return true;
		} else
		{
			return initialized;
		}
	}

	private ComponentProperties properties;
	private EnvironmentElements environment;
	@CoreComponent
	public HashMap<ComponentClassification, ArrayList<Component>> localSubElementMap;
	@CoreComponent
	public HashMap<ComponentClassification, ArrayList<Component>> localElementMap;

	public Component(String name, ComponentClassification type)
	{
		properties = new ComponentProperties(name, type);

		initialized = false;

		initializeMaps();
	}

	public ArrayList<Component> getAllComponents(boolean include_global)
	{
		ArrayList<Component> allComponents = new ArrayList<Component>();

		if (include_global)
		{
			if (localSubElementMap != null)
			{
				for (ArrayList<Component> components : localSubElementMap.values())
				{
					allComponents.addAll(components);
				}
			}
		} else
		{
			for (ArrayList<Component> components : localElementMap.values())
			{
				allComponents.addAll(components);
			}
		}

		return allComponents;
	}

	public void clearMapsIfEmpty()
	{
		System.out.println("Size components : " + getAllComponents(true).size());
		if (getAllComponents(true).size() == 0)
		{
			localSubElementMap.clear();
			localElementMap.clear();
		}
	}

	private void initializeMaps()
	{
		localSubElementMap = initializeElementMap();
		localElementMap = initializeElementMap();
	}

	private static HashMap<ComponentClassification, ArrayList<Component>> initializeElementMap()
	{
		HashMap<ComponentClassification, ArrayList<Component>> elementMap = new HashMap<ComponentClassification, ArrayList<Component>>();
		for (ComponentClassification c : ComponentClassification.values())
		{
			elementMap.put(c, new ArrayList<Component>());
		}
		return elementMap;
	}

	public void loadAllComponents()
	{
		loadAllComponents(this);

		//clearMapsIfEmpty();
		//initializeComponents(true);
	}

	protected void loadAllComponents(Object sys_obj)
	{
		Object sysObj = sys_obj;
		Class<? extends Object> superClass = sys_obj.getClass();

		{
			while (superClass != Object.class)
			{
				//System.out.println("Class " + superClass + " Object " + sysObj.toString());

				((Component) sysObj).loadComponents(sysObj);
				superClass = superClass.getSuperclass();
				Object newSysObj = superClass.cast(sysObj);
				sysObj = newSysObj;
				//System.out.println("Class " + superClass);
				//	System.out.println(superClass.getName());

			}
		}
		//	objectScanned(sys_obj);
		//storeSubComponents((Component) sysObj);
		//		Object currentObj = this;
		//		Class currentClass = this.getClass();
		//		while (!currentClass.equals(Object.class))
		//		{
		//			Object newObj = currentClass.cast(currentObj);
		//			loadComponents(currentObj);
		//			currentClass = currentObj.getClass().getSuperclass();
		//
		//			System.out.println("Class " + currentClass);
		//			currentObj = currentClass.cast(newObj);
		//		}
	}

	private void loadComponents(Object load)
	{

		for (Object field : FieldFinder.getObjectFieldValues(load, true))
		{

			if (field != null)
			{
				{
					//System.out.println(field.toString());

					//for (Object allCurrent : ComponentLocator.components(field, Component.class))
					//			{
					//				((Component) allCurrent).loadAllComponents();
					//				//((Component) field).storeSubComponents((Component) allCurrent);
					//			}
					//System.out.println(getContainerComponents(field, Component.class));
					for (Component containerField : getContainerComponents(field, Component.class))
					{
						loadAllComponents(containerField);
						((Component) load).storeSubComponents(containerField);
					}
					if (FieldFinder.containsSuper(field, Component.class))
					{
						//((Component) load).storeComponent((Component) field, true);
						//storeSubComponents((Component) field);

						{
							loadAllComponents(field);
							((Component) load).storeSubComponents((Component) field);
						}

						//	((Component) field).loadAllComponents();
						//	storeComponent((Component) field, true);
					}
				}
			}

		}
		//	
	}

	private void storeComponent(Component component, boolean local)
	{
		if (local)
		{
			if (!localElementMap.get(component.properties.getClassification()).contains(component))
			{
				localElementMap.get(component.properties.getClassification()).add(component);//..getProperties().getClassification()).add(allCurrent))));
			}
		}
		if (!localSubElementMap.get(component.properties.getClassification()).contains(component))
		{
			//	System.out.println(component);
			localSubElementMap.get(component.properties.getClassification()).add(component);//..getProperties().getClassification()).add(allCurrent))));
		}

	}

	protected void storeSubComponents(Component component)
	{
		storeComponent(component, true);
		//System.out.println("All Components " + component.getAllComponents(true));
		for (Component subComponent : component.getAllComponents(true))
		{
			storeComponent(subComponent, false);
		}
	}

	public void addSubSystem(HybridSystem sys)
	{
		addSubSystem(sys, 1);

	}

	public HybridSystem prepSys(HybridSystem sys)
	{
		HybridSystem prepped = sys;
		prepped.loadAllComponents();
		if (!sys.equals(this))
		{
			this.storeSubComponents(prepped);
		}
		return prepped;
	}

	public void addSubSystem(HybridSystem sys, Integer quantity)
	{
		String outp = (XMLParser.serializeObject(this) + "\n\n");
		HybridSystem newSys = prepSys(sys);
		for (Integer sysNum = 0; sysNum < quantity; sysNum++)
		{
			HybridSystem cloneSys = (HybridSystem) ObjectCloner.xmlClone(newSys);//clone(sys);//, this.hierarchy());
			if (sys.equals(this))
			{
				this.storeSubComponents(cloneSys);
			}
			//	loadAllComponents(cloneSys);
			//storeComponent(cloneSys, true);
			//this.storeSubComponents(cloneSys);
			//sys.s(sys);

			for (Component component : cloneSys.getAllComponents(true))
			{
				if (Component.getInitialized(component) == null)
				{
					Component.setInitialized(component, true);
				} else
				{
					Component.setInitialized(component, false);
				}
			}
			newSys = cloneSys;
			outp += "\n\n" + (XMLParser.serializeObject(this) + "\n\n");
			outp += "\n\n" + (XMLParser.serializeObject(cloneSys) + "\n\n");
			FileSystemOperator.createOutputFile("./addSubSystem.xml", outp);
			System.out
			.println((this.getAllComponents(true).toArray(new Component[this.getAllComponents(true).size()]).length));
			//loadComponents(newSys);
		}

	}

	public ArrayList<Component> getComponents(ComponentClassification type, boolean global)
	{

		return getComponents(type, Component.class, global);

	}

	public <T> ArrayList<T> getComponents(ComponentClassification type, Class<T> type_class, boolean global)
	{
		// TODO Auto-generated method stub
		if (global)
		{
			return (ArrayList<T>) localSubElementMap.get(type);
		} else
		{
			return (ArrayList<T>) localElementMap.get(type);
		}
	}

	public ArrayList<Component> getComponents(DataCategory category, boolean global)
	{
		// TODO Auto-generated method stub
		for (ComponentClassification type : category.subTypes)
		{
			if (global)
			{
				return localSubElementMap.get(type);
			} else
			{
				return localElementMap.get(type);
			}
		}
		return null;
	}

	public EnvironmentElements getEnvironment()
	{
		return environment;
		//return environment;
	}

	public static void setEnvironment(Component component, EnvironmentElements environment)
	{
		EnvironmentElements accessor = environment;
		component.environment = accessor;
	}

	public ComponentProperties getProperties()
	{
		return properties;
	}

	public void saveComponentToFile(String directory_path, String file_name)
	{
		Object clonedComponent = ObjectCloner.xmlClone(this);
		((Component) clonedComponent).loadAllComponents();
		((Component) clonedComponent).storeSubComponents(((Component) clonedComponent));
		((Component) clonedComponent).clearMapsIfEmpty();
		for (Component subComponent : ((Component) clonedComponent).getAllComponents(true))
		{
			subComponent.clearMapsIfEmpty();
		}
		FileSystemOperator.createOutputFile(new File(directory_path, file_name),
		XMLParser.serializeObject(clonedComponent));

	}

	public static void setInitialized(Component component, Boolean initialized)
	{
		component.initialized = initialized;
	}

	public static Boolean getInitialized(Component component)
	{
		return component.initialized;
	}

	public static ArrayList<Object> getMapOrListElements(Object obj)
	{
		ArrayList<Object> values = new ArrayList<Object>();
		try
		{
			Collection<Object> objects = ((Map) obj).values();
			values.addAll(objects);
		} catch (Exception notMap)
		{
			try
			{
				Collection<Object> objects = ((List) obj);
				values.addAll(objects);
			} catch (Exception notList)
			{

			}
		}
		return values;
	}

	public static <T> ArrayList<T> getContainerComponents(Object container, Class<T> search)
	{
		//System.out.println("h" + container);

		ArrayList<T> components = new ArrayList<T>();

		if (!FieldFinder.containsInterface(container, CoreComponent.class))
		{
			try
			{
				//	for (T entry : ((HashMap<?, T>) (HashMap.class.cast(container))).values())
				for (T entry : ((HashMap<?, T>) container).values())
				{
					//System.out.println("h" + container);
					if (FieldFinder.containsSuper(entry, search))
					{
						//	if (!objectScanned(entry))
						{
							components.add((T) entry);
						}
					}
				}
			} catch (Exception e)
			{
				try
				{
					//		for (T entry : ((ArrayList<T>) (ArrayList.class.cast(container))))
					for (T entry : ((ArrayList<T>) container))
					{
						//System.out.println("h" + container);
						if (FieldFinder.containsSuper(entry, search))
						{
							//	if (!objectScanned(entry))
							{
								components.add((T) entry);
							}
						}
					}
				} catch (Exception ee)
				{

				}
			}
		}

		return components;
	}
}
