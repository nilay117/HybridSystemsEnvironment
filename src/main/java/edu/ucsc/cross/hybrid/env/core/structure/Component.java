package edu.ucsc.cross.hybrid.env.core.structure;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.execution.Initializer;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hybrid.env.core.components.HybridSystem;

public abstract class Component implements Initializer//implements ComponentInterface
{

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
	public HashMap<ComponentClassification, ArrayList<Component>> globalElementMap;
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
		if (globalElementMap != null)
		{
			if (include_global)
			{
				for (ArrayList<Component> components : globalElementMap.values())
				{
					allComponents.addAll(components);
				}
			} else
			{
				for (ArrayList<Component> components : localElementMap.values())
				{
					allComponents.addAll(components);
				}
			}
		}
		return allComponents;
	}

	public void clearMapsIfEmpty()
	{
		System.out.println("Size components : " + getAllComponents(true).size());
		if (getAllComponents(true).size() == 0)
		{
			globalElementMap.clear();
			localElementMap.clear();
		}
	}

	private void initializeMaps()
	{
		globalElementMap = initializeMap();
		localElementMap = initializeMap();
	}

	private HashMap<ComponentClassification, ArrayList<Component>> initializeMap()
	{
		localElementMap = new HashMap<ComponentClassification, ArrayList<Component>>();
		for (ComponentClassification c : ComponentClassification.values())
		{
			localElementMap.put(c, new ArrayList<Component>());
		}
		return localElementMap;
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
			//System.out.println(field.toString());

			//for (Object allCurrent : ComponentLocator.components(field, Component.class))
			//			{
			//				((Component) allCurrent).loadAllComponents();
			//				//((Component) field).storeSubComponents((Component) allCurrent);
			//			}
			if (FieldFinder.containsSuper(field, Component.class))
			{
				//((Component) load).storeComponent((Component) field, true);
				//storeSubComponents((Component) field);
				loadAllComponents(field);
				((Component) load).storeSubComponents((Component) field);

				//	((Component) field).loadAllComponents();
				//	storeComponent((Component) field, true);
			}
		}

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
		if (!globalElementMap.get(component.properties.getClassification()).contains(component))
		{
			//	System.out.println(component);
			globalElementMap.get(component.properties.getClassification()).add(component);//..getProperties().getClassification()).add(allCurrent))));
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

	//@Override
	public ArrayList<Component> getComponents(ComponentClassification type, boolean global)
	{
		// TODO Auto-generated method stub
		if (global)
		{
			return globalElementMap.get(type);
		} else
		{
			return localElementMap.get(type);
		}
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
}
