package edu.ucsc.cross.hybrid.env.core.components;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import edu.ucsc.cross.hybrid.env.core.containers.EnvironmentContent;
import edu.ucsc.cross.hybrid.env.core.properties.ComponentProperties;

public abstract class Component implements Initializer //implements ComponentInterface
{

	@CoreComponent
	public HashMap<Class<?>, ArrayList<Component>> childComponentMap;
	@CoreComponent
	public HashMap<Class<?>, ArrayList<Component>> descendantComponentMap;
	@CoreComponent
	private ArrayList<Component> childComponents; // direct children of this component (no childrens children)
	@CoreComponent
	private ArrayList<Component> descendantComponents; // all children of this component (all children & childrens children)
	@CoreComponent
	public HashMap<Class<?>, ArrayList<Object>> childObjectMap;
	@CoreComponent
	public HashMap<Class<?>, ArrayList<Object>> descendantObjectMap;
	@CoreComponent
	private ArrayList<Object> childObjects; // direct children of this component (no childrens children)
	@CoreComponent
	private ArrayList<Object> descendantObjects; // all children of this component (all children & childrens children)
	@CoreComponent
	private EnvironmentContent environment; // environment that the component is in
	@CoreComponent
	private Boolean initialized;
	@CoreComponent
	private Component parentComponent; // parent component
	@CoreComponent
	private ComponentProperties properties; // properties of the component

	/*
	 * Constructor that defines the name and base class of the component
	 * 
	 * @param title - title of the component
	 * 
	 * @param base_class - base class of the component
	 */
	public Component(String title, Class<?> base_class)
	{
		properties = new ComponentProperties(title, base_class);
		setup();
	}

	/*
	 * Constructor that defines the name of the component with this class
	 * (Component) as the base class. This constructor is used to create
	 * components that are not based off any of the core components:
	 * Behavior,Data,DataSet, and HybridSystem.
	 * 
	 * @param title - title of the component
	 */
	public Component(String title)
	{
		properties = new ComponentProperties(title, Component.class);
		setup();
	}

	/*
	 * Adds a single sub-component to this component. This is used to add
	 * components that are not explicitly defined in the main class, which
	 * allows for variations without modifying the main component code itself
	 * 
	 * @param component - component to be added
	 */
	public <T extends Component> void addComponent(T component)
	{
		addComponent(component, 1);
	}

	/*
	 * Adds a number of sub-components to this component. This is used to add
	 * components that are not explicitly defined in the main class, which
	 * allows for variations without modifying the main component code itself.
	 * This method allows any number of duplicate components to be added
	 * 
	 * @param component - component to be added
	 * 
	 * @param quantity - number of components to be added
	 */
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

	public ArrayList<Component> getAllllComponents(boolean global)
	{
		ArrayList<Component> allComponents = new ArrayList<Component>();
		try
		{
			if (global)
			{
				for (ArrayList<Component> components : descendantComponentMap.values())
				{
					allComponents.addAll(components);
				}
			} else
			{
				for (ArrayList<Component> components : childComponentMap.values())
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
			return descendantComponents;

		} else
		{
			return childComponents;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> getComponents(Class<T> component_class, boolean include_children)
	{
		ArrayList<T> components = new ArrayList<T>();
		try
		{
			if (include_children)
			{
				components = (ArrayList<T>) descendantComponentMap.get(component_class);
			} else
			{
				components = (ArrayList<T>) childComponentMap.get(component_class);
			}
		} catch (Exception noComponents)
		{
			components = null;
		}
		if (components == null)
		{
			ArrayList<Component> componentz = scanForComponents(component_class, include_children);
			if (include_children)
			{
				descendantComponentMap.put(component_class, componentz);
			} else
			{
				childComponentMap.put(component_class, componentz);
			}
			components = (ArrayList<T>) componentz;
			//components = getComponents(component_class, include_children);
		}

		return components;
	}

	//	@SuppressWarnings("unchecked")
	//	public <T> ArrayList<T> getObjects(Class<T> component_class, boolean include_children)
	//	{
	//		ArrayList<T> components = new ArrayList<T>();
	//		try
	//		{
	//			if (include_children)
	//			{
	//				components = (ArrayList<T>) descendantObjectMap.get(component_class);
	//			} else
	//			{
	//				components = (ArrayList<T>) childObjectMap.get(component_class);
	//			}
	//		} catch (Exception noComponents)
	//		{
	//			components = null;
	//		}
	//		if (components == null)
	//		{
	//			ArrayList<Object> componentObjects = scanForObjects(component_class, include_children);
	//			System.out.println(componentObjects.toString());
	//			if (include_children)
	//			{
	//				descendantObjectMap.put(component_class, componentObjects);
	//			} else
	//			{
	//				childObjectMap.put(component_class, componentObjects);
	//			}
	//			components = new ArrayList<T>();
	//			components.addAll((ArrayList<T>) componentObjects);
	//			//components = getComponents(component_class, include_children);
	//		}
	//
	//		return components;
	//	}

	public EnvironmentContent getEnvironment()
	{
		return environment;
		//return environment;
	}

	public ComponentProperties getProperties()
	{
		return properties;
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

	public void saveComponentToFile(String directory_path, String file_name)
	{
		Object clonedComponent = ObjectCloner.xmlClone(this);
		((Component) clonedComponent).load();//.loadAllComponents();
		//	((Component) clonedComponent).storeSubComponents(((Component) clonedComponent));
		//((Component) clonedComponent).clearMapsIfEmpty();
		//		for (Component subComponent : ((Component) clonedComponent).getAllllComponents(true))
		//		{
		//			subComponent.clearMapsIfEmpty();
		//		}
		FileSystemOperator.createOutputFile(new File(directory_path, file_name),
		XMLParser.serializeObject(clonedComponent));

	}

	private void initializeContainers()
	{
		descendantComponentMap = new HashMap<Class<?>, ArrayList<Component>>();
		childComponentMap = new HashMap<Class<?>, ArrayList<Component>>();
		descendantObjectMap = new HashMap<Class<?>, ArrayList<Object>>();
		childObjectMap = new HashMap<Class<?>, ArrayList<Object>>();
		childComponents = new ArrayList<Component>();
		descendantComponents = new ArrayList<Component>();
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
		return components;
	}

	private void processComponent(Component parent, Component field)
	{
		field.parentComponent = parent;
		field.loadHierarchyComponents();
		parent.storeComponent(field, true);
	}

	private void processContainer(Component parent, Object container)
	{
		for (Object content : getContainerContents(container))
		{
			//System.out.println(content);
			processField(parent, content);
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

	//	public ArrayList<Component> getData(DataCategory category, boolean global)
	//	{
	//		// TODO Auto-generated method stub
	//		for (ComponentClassification type : category.subTypes)
	//		{
	//			if (global)
	//			{
	//				return descendantComponentMap.get(type);
	//			} else
	//			{
	//				return childComponentMap.get(type);
	//			}
	//		}
	//		return null;
	//	}

	private void processFields(Component parent, ArrayList<Object> fields)
	{
		for (Object field : fields)
		{
			processField(parent, field);
		}
	}

	private void setup()
	{
		initialized = false;
		initializeContainers();
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
		processFields((Component) sysObj, allFields);
	}

	protected void storeComponent(Component component, boolean local)
	{
		if (component != null)
		{
			if (local)
			{
				if (!childComponents.contains(component))
				{
					childComponents.add(component);
				}
				if (!childComponentMap.containsKey(component.properties.baseComponentClass))
				{
					childComponentMap.put(component.properties.baseComponentClass, new ArrayList<Component>());//..getProperties().getClassification()).add(allCurrent))));
				}
				if (!childComponentMap.get(component.properties.baseComponentClass).contains(component))
				{
					childComponentMap.get(component.properties.baseComponentClass).add(component);
				}
			}
			if (!descendantComponents.contains(component))
			{
				descendantComponents.add(component);
			}
			if (!descendantComponentMap.containsKey(component.properties.baseComponentClass))
			{
				descendantComponentMap.put(component.properties.baseComponentClass, new ArrayList<Component>());//..getProperties().getClassification()).add(allCurrent))));
			}
			if (!descendantComponentMap.get(component.properties.baseComponentClass).contains(component))
			{
				descendantComponentMap.get(component.properties.baseComponentClass).add(component);
			}
		}
	}

	public static Component getComponentFromFile(String file_path)
	{
		Component component = null;
		try
		{

			component = (Component) XMLParser.getObject(file_path);
		} catch (Exception badComponent)
		{
			badComponent.printStackTrace();
		}
		return component;

	}

	private <T> ArrayList<Component> scanForComponents(Class<T> scan_class, boolean global)
	{
		ArrayList<Component> foundObjects = new ArrayList<Component>();
		ArrayList<Component> allComponents = new ArrayList<Component>();
		if (global)
		{
			allComponents = descendantComponents;
		} else
		{
			allComponents = childComponents;
		}
		for (Component component : allComponents)
		{
			System.out
			.println(component.getClass().getSimpleName() + " " + component.getClass().getInterfaces().length);
			if (FieldFinder.containsSuper(component, scan_class))
			{
				foundObjects.add(component);
			} else if (Arrays.asList(component.getClass().getInterfaces()).contains(scan_class))
			{
				foundObjects.add(component);
			}
		}
		return foundObjects;
	}

	public static ArrayList<Object> getContainerContents(Object container)
	{
		ArrayList<Object> components = new ArrayList<Object>();

		if (!FieldFinder.containsInterface(container, CoreComponent.class))
		{
			try
			{
				for (Object entry : ((ArrayList) container))
				{
					components.add(entry);
				}

			} catch (Exception notMap)
			{
				//notMap.printStackTrace();
				try
				{
					for (Object entry : (HashMap.class.cast(container)).values())
					{
						components.add(entry);
					}
				} catch (Exception ee)
				{
					//	ee.printStackTrace();
				}
			}
		}

		return components;

	}

	//	
	public static <T> ObjectType getElementType(T object)
	{
		ObjectType type = ObjectType.UNKNOWN;
		if (object != null)
		{
			if (FieldFinder.containsSuper(object, HashMap.class) || FieldFinder.containsSuper(object, ArrayList.class))
			{
				type = ObjectType.CONTAINER;
			} else if (FieldFinder.containsSuper(object, Component.class))
			{
				type = ObjectType.COMPONENT;
			}
		}
		return type;
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

	public static Boolean isInitialized(Component component)
	{
		return component.initialized;
	}

	public static void setEnvironment(Component component, EnvironmentContent environment)
	{
		EnvironmentContent accessor = environment;
		component.environment = accessor;
	}

	public static void setInitialized(Component component, Boolean initialized)
	{
		component.initialized = initialized;
	}

	private static enum ObjectType
	{
		COMPONENT,
		CONTAINER,
		JAVA,
		UNKNOWN;

	}
}
