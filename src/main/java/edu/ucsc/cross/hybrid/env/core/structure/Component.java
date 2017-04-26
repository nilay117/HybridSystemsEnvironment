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

	@CoreComponent
	private ComponentProperties properties;
	@CoreComponent
	private EnvironmentContents environment;
	@CoreComponent
	private Component parent;
	@CoreComponent
	private ArrayList<Component> rootComponents;
	@CoreComponent
	private ArrayList<Component> childComponents;
	@CoreComponent
	public HashMap<Class<?>, ArrayList<Component>> childElementMap;
	@CoreComponent
	public HashMap<Class<?>, ArrayList<Component>> rootElementMap;

	public Component(String name, ComponentClassification type)
	{
		properties = new ComponentProperties(name, type);
		initialized = false;
		setup();
	}

	public Component(String name, Class<?> component_base_class)
	{
		properties = new ComponentProperties(name, component_base_class);
		setup();

	}

	private void setup()
	{
		initialized = false;
		initializeContainers();
	}

	public void load()
	{
		loadHierarchyComponents();
		ArrayList<Component> init = new ArrayList<Component>();
		init.addAll(getComponents(true));
		for (Component component : init)
		{
			component.load();
			for (Component componentChild : component.getComponents(true))
			{
				storeComponent(componentChild, false);
			}
		}

	}

	public ArrayList<Component> getAllllComponents(boolean global)
	{
		ArrayList<Component> allComponents = new ArrayList<Component>();
		try
		{
			if (global)
			{
				for (ArrayList<Component> components : childElementMap.values())
				{
					allComponents.addAll(components);
				}
			} else
			{
				for (ArrayList<Component> components : rootElementMap.values())
				{
					allComponents.addAll(components);
				}
			}
		} catch (Exception noComponents)
		{

		}

		return allComponents;
	}

	public ArrayList<Component> getComponents(boolean include_children)
	{
		if (include_children)
		{
			return childComponents;

		} else
		{
			return rootComponents;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> ArrayList<T> getComponents(Class<T> component_class, boolean include_children)
	{
		ArrayList<T> components = new ArrayList<T>();
		try
		{
			if (include_children)
			{
				components = (ArrayList<T>) childElementMap.get(component_class);
			} else
			{
				components = (ArrayList<T>) rootElementMap.get(component_class);
			}
		} catch (Exception noComponents)
		{

		}
		if (components == null)
		{
			components = (ArrayList<T>) loadMap(component_class, include_children);
			//components = getComponents(component_class, include_children);
		}

		return components;
	}

	private void initializeContainers()
	{
		childElementMap = new HashMap<Class<?>, ArrayList<Component>>();
		rootElementMap = new HashMap<Class<?>, ArrayList<Component>>();
		rootComponents = new ArrayList<Component>();
		childComponents = new ArrayList<Component>();
	}

	protected void loadHierarchyComponents()
	{
		Object sysObj = this;
		Class<? extends Object> superClass = sysObj.getClass();
		ArrayList<Object> allFields = new ArrayList<Object>();
		{
			while (superClass != Object.class)
			{
				//System.out.println("Class " + superClass + " Object " + sysObj.toString());
				for (Object field : FieldFinder.getObjectFieldValues(sysObj, true))
				{
					if (!allFields.contains(field))
					{
						allFields.add(field);
					}
				}
				superClass = superClass.getSuperclass();
				Object newSysObj = superClass.cast(sysObj);
				sysObj = newSysObj;
				//System.out.println("Class " + superClass);
				//	System.out.println(superClass.getName());

			}
		}
		processFields(this, allFields);
	}

	private void processFields(Component parent, ArrayList<Object> fields)
	{
		for (Object field : fields)
		{
			processField(parent, field);
		}
	}

	private void processField(Component parent, Object field)
	{
		switch (getElementType(field))
		{
		case COMPONENT:
			processComponent(parent, (Component) field);
			break;
		case CONTAINER:
			processContainer(parent, field);
			break;
		default:
			break;
		}
	}

	private void processContainer(Component parent, Object container)
	{
		for (Object content : getContainerContents(container))
		{
			processField(parent, content);
		}
	}

	private void processComponent(Component parent, Component field)
	{
		field.parent = parent;
		field.loadHierarchyComponents();
		parent.storeComponent(field, true);
	}

	//	private void loadComponents(Object load)
	//	{
	//
	//		for (Object field : FieldFinder.getObjectFieldValues(load, true))
	//		{
	//			switch (getElementType()
	//				//System.out.println(field.toString());
	//
	//				//for (Object allCurrent : ComponentLocator.components(field, Component.class))
	//				//			{
	//				//				((Component) allCurrent).loadAllComponents();
	//				//				//((Component) field).storeSubComponents((Component) allCurrent);
	//				//			}
	//				//System.out.println(getContainerComponents(field, Component.class));
	//
	//				//				if (FieldFinder.containsSuper(field, Component.class))
	//				//				{
	//				//					//((Component) load).storeComponent((Component) field, true);
	//				//					//storeSubComponents((Component) field);
	//				//
	//				//					{
	//				//						loadAllComponents(field);
	//				//						((Component) load).storeSubComponents((Component) field);
	//				//					}
	//				//
	//				//					//	((Component) field).loadAllComponents();
	//				//					//	storeComponent((Component) field, true);
	//				//				} else if (isContainer(field))
	//				//				{
	//				//					for (Object containerField : getContainerContents(field)
	//				//					{
	//				//						if (FieldFinder.containsSuper(containerField)
	//				//						loadAllComponents(containerField);
	//				//						((Component) load).storeSubComponents(containerField);
	//				//					}
	//				//				}
	//			}
	//		}

	public static enum ObjectType
	{
		COMPONENT,
		CONTAINER,
		JAVA,
		UNKNOWN;

	}

	//	
	public static <T> ObjectType getElementType(T object)
	{
		ObjectType type = ObjectType.UNKNOWN;
		if (object != null)
		{
			if (FieldFinder.containsSuper(object, Map.class) || FieldFinder.containsSuper(object, List.class))
			{
				type = ObjectType.CONTAINER;
			} else if (FieldFinder.containsSuper(object, Component.class))
			{
				type = ObjectType.COMPONENT;
			}
		}
		return type;
	}

	public <T extends Component> void addComponent(T component)
	{
		addComponent(component, 1);
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> void addComponent(T component, Integer quantity)
	{
		T initialClone = (T) ObjectCloner.xmlClone(component);
		for (Integer ind = 0; ind < quantity; ind++)
		{
			T clonedComponent = (T) ObjectCloner.xmlClone(initialClone);
			storeComponent(clonedComponent, true);
		}
	}

	private ArrayList<Component> loadMap(Class<?> component_class, boolean children)
	{
		ArrayList<Component> components = new ArrayList<Component>();
		for (Component component : getComponents(children))
		{
			if (FieldFinder.containsSuper(component, component_class))
			{
				components.add(component);//..getProperties().getClassification()).add(allCurrent))));
			}
		}
		//mapComponents(component_class, components, children);
		return components;
	}

	//	private void mapComponents(Class<?> component_class, ArrayList<Component> components, boolean children)
	//	{
	//
	//		if (!children)
	//		{
	//
	//			if (!rootElementMap.get(component.properties.getClassification()).contains(component))
	//			{
	//				rootElementMap.get(component.properties.getClassification()).add(component);//..getProperties().getClassification()).add(allCurrent))));
	//			}
	//		}
	//		//	}if(!childElementMap.get(component.properties.getClassification()).contains(component))
	//
	//		//{
	//		//	System.out.println(component);
	//		//childElementMap.get(component.properties.getClassification()).add(component);//..getProperties().getClassification()).add(allCurrent))));
	//		//}
	//
	//	}

	protected void storeComponent(Component component, boolean local)
	{
		if (component != null)
		{
			if (local)
			{
				if (!rootComponents.contains(component))
				{
					rootComponents.add(component);
				}
				if (!rootElementMap.containsKey(component.properties.baseComponentClass))
				{
					rootElementMap.put(component.properties.baseComponentClass, new ArrayList<Component>());//..getProperties().getClassification()).add(allCurrent))));
				}
				if (!rootElementMap.get(component.properties.baseComponentClass).contains(component))
				{
					rootElementMap.get(component.properties.baseComponentClass).add(component);
				}
			}
			if (!childComponents.contains(component))
			{
				childComponents.add(component);
			}
			if (!childElementMap.containsKey(component.properties.baseComponentClass))
			{
				childElementMap.put(component.properties.baseComponentClass, new ArrayList<Component>());//..getProperties().getClassification()).add(allCurrent))));
			}
			if (!childElementMap.get(component.properties.baseComponentClass).contains(component))
			{
				childElementMap.get(component.properties.baseComponentClass).add(component);
			}
		}
	}

	public ArrayList<Component> getData(DataCategory category, boolean global)
	{
		// TODO Auto-generated method stub
		for (ComponentClassification type : category.subTypes)
		{
			if (global)
			{
				return childElementMap.get(type);
			} else
			{
				return rootElementMap.get(type);
			}
		}
		return null;
	}

	public EnvironmentContents getEnvironment()
	{
		return environment;
		//return environment;
	}

	public static void setEnvironment(Component component, EnvironmentContents environment)
	{
		EnvironmentContents accessor = environment;
		component.environment = accessor;
	}

	public ComponentProperties getProperties()
	{
		return properties;
	}

	public void saveComponentToFile(String directory_path, String file_name)
	{
		Object clonedComponent = ObjectCloner.xmlClone(this);
		//((Component) clonedComponent).loadAllComponents();
		((Component) clonedComponent).storeSubComponents(((Component) clonedComponent));
		((Component) clonedComponent).clearMapsIfEmpty();
		for (Component subComponent : ((Component) clonedComponent).getAllllComponents(true))
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

	public static ArrayList<Object> getContainerContents(Object container)
	{
		ArrayList<Object> components = new ArrayList<Object>();

		if (!FieldFinder.containsInterface(container, CoreComponent.class))
		{
			try
			{
				for (Object entry : ((HashMap<?, ?>) container).values())
				{
					components.add(entry);
				}
			} catch (Exception notMap)
			{

				try
				{
					for (Object entry : ((ArrayList<?>) container))
					{
						components.add(entry);
					}
				} catch (Exception ee)
				{

				}
			}
		}

		return components;

	}

	//// Depreciating 

	public void clearMapsIfEmpty()
	{
		System.out.println("Size components : " + getAllllComponents(true).size());
		if (getAllllComponents(true).size() == 0)
		{
			childElementMap.clear();
			rootElementMap.clear();
		}
	}

	protected void storeSubComponents(Component component)
	{
		storeComponent(component, true);
		//System.out.println("All Components " + component.getAllComponents(true));
		for (Component subComponent : component.getAllllComponents(true))
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
		//prepped.loadAllComponents();
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

			for (Component component : cloneSys.getAllllComponents(true))
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
			System.out.println(
			(this.getAllllComponents(true).toArray(new Component[this.getAllllComponents(true).size()]).length));
			//loadComponents(newSys);
		}

	}
}
