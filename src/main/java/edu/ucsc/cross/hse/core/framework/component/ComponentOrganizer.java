package edu.ucsc.cross.hse.core.framework.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.manipulation.ObjectCloner;

/*
 * This class contains structures that define the hierarchy of additional
 * components that are related to a particular component. The purpose of this
 * class is to make it easy to access components with search specifications, ie
 * find all instances of a specific class in any descendant.
 * 
 * The lists and mappings contain sub-components of this component, which are
 * the components defined within a particular component or any of its
 * sub-components. The parent and the environment of this particular are also
 * available within this structure.
 */
public class ComponentOrganizer
{

	// Mapping of all declared components indexed by class
	public HashMap<Class<?>, ArrayList<Component>> declaredComponentMap;

	// Mapping of all components from all descendants indexed by class
	public HashMap<Class<?>, ArrayList<Component>> descendantComponentMap;

	// List of all components from all descendants
	private ArrayList<Component> declaredAdjunctComponentList;

	// List of all components from all descendants
	private ArrayList<Component> declaredAdjunctDescendantComponentList;

	// List of all declared components
	private ArrayList<Component> declaredComponentList;
	// List of all components from all descendants
	private ArrayList<Component> descendantComponentList;

	// key that links the component to the global environment that it is
	// contained in. This keeps the component size smaller when being copied or
	// saved, and allows for multiple environments to be running simultaneously
	private String environmentKey;

	// The parent that declared this component
	private Component parentComponent;

	// Pointer to this component
	private Component self;

	/*
	 * Adds a single sub-component to this component. This is used to add
	 * components that are not explicitly defined in the main class, which
	 * allows for variations without modifying the main component code itself
	 *
	 * @param component - component to be added
	 */
	public <T extends Component> ArrayList<Component> addComponent(T component)
	{
		return addComponent(component, 1);
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
	public <T extends Component> ArrayList<Component> addComponent(T component, Integer quantity)
	{
		ArrayList<Component> ret = new ArrayList<Component>();

		T initialClone = (T) ObjectCloner.xmlClone(component);
		// T initialClone = ObjectCloner.cloner.deepClone(component);
		for (Integer ind = 0; ind < quantity; ind++)
		{
			T clonedComponent = (T) ObjectCloner.xmlClone(initialClone);
			storeComponent(clonedComponent, true);
			ret.add(clonedComponent);
			initialClone = clonedComponent;
		}
		addAllUndeclaredComponents(ret);

		return ret;
	}

	/*
	 * Get a mapping of components indexed by their names
	 */
	public <S extends Component> HashMap<String, S> getComponentMapByName(Class<S> component_class,
	boolean include_children)
	{
		ArrayList<S> components = getObjects(component_class, include_children);
		HashMap<String, S> componentMap = new HashMap<String, S>();
		for (S component : components)
		{
			componentMap.put(component.getLabels().name, component);
		}
		return componentMap;
	}

	/*
	 * Get all components, can include decendents as well depending on the input
	 */
	public ArrayList<Component> getComponents(boolean include_children)
	{
		if (include_children)
		{
			return descendantComponentList;

		} else
		{
			return declaredComponentList;
		}
	}

	/*
	 * Gets components of a certain class, can also include dependents as well
	 */
	public ArrayList<Component> getComponents(boolean include_children, Class... classes)
	{
		return getObjects(Component.class, include_children, classes);
	}

	/*
	 * Get all components decending from this component
	 */
	public HashMap<String, Component> getDeclaredDescendants()
	{
		HashMap<String, Component> descendants = new HashMap<String, Component>();
		for (Component desc : declaredAdjunctComponentList)
		{
			descendants.put(desc.toString(), desc);
		}
		return descendants;
	}

	/*
	 * Get any objects that match the search criteria defined by the inputs
	 */
	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> getObjects(Class<T> component_class, boolean include_children, Class<?>... classes)
	{
		ArrayList<T> allComponents = new ArrayList<T>();
		Class[] matchingClasses = classes;
		if (matchingClasses.length == 0)
		{
			matchingClasses = new Class[]
			{ component_class };
		}
		for (Class clas : matchingClasses)
		{
			ArrayList<T> componentz;
			try
			{
				componentz = getObjects(component_class, include_children, clas);
				allComponents.addAll(componentz);

			} catch (Exception noComponents)
			{
				noComponents.printStackTrace();
				componentz = null;
			}
			if (componentz == null)
			{
				ArrayList<Component> componente = scanForComponents(clas, include_children);
				if (include_children)
				{
					descendantComponentMap.put(clas, componente);
				} else
				{
					declaredComponentMap.put(clas, componente);
				}
				allComponents.addAll((ArrayList<T>) componente);
				// components = getComponents(component_class,
				// include_children);
			}
		}
		return allComponents;
	}

	/*
	 * Get the parent component of this component (declaring class)
	 */
	public Component getParentComponent()
	{
		return parentComponent;
	}

	/*
	 * get all components that are not declared in a class, but added later on
	 * through software
	 */
	private void addAllUndeclaredComponents(ArrayList<Component> undeclareds)
	{

		for (Component undeclared : undeclareds)
		{
			try
			{

				undeclared.getContents().constructTree();
				if (!declaredAdjunctComponentList.contains(undeclared))
				{
					declaredAdjunctComponentList.add(undeclared);
				}
				for (Component undeclaredDescendant : undeclared.getContents().getComponents(true))
				{
					if (!declaredAdjunctDescendantComponentList.contains(undeclaredDescendant))
					{
						declaredAdjunctDescendantComponentList.add(undeclaredDescendant);
					}
				}
			} catch (Exception e)
			{

			}
		}
	}

	/*
	 * Get all objects that match specific filters
	 */
	@SuppressWarnings("unchecked")
	private <T> ArrayList<T> getObjects(Class<T> component_class, boolean include_children, Class<?> search_class)
	{
		ArrayList<T> components = new ArrayList<T>();
		try
		{
			if (include_children)
			{
				components = (ArrayList<T>) descendantComponentMap.get(search_class);
			} else
			{
				components = (ArrayList<T>) declaredComponentMap.get(search_class);
			}
		} catch (Exception noComponents)
		{
			components = null;
		}
		if (components == null)
		{
			ArrayList<Component> componentz = scanForComponents(search_class, include_children);
			if (include_children)
			{
				descendantComponentMap.put(search_class, componentz);
			} else
			{
				declaredComponentMap.put(search_class, componentz);
			}
			components = (ArrayList<T>) componentz;
			// components = getComponents(component_class, include_children);
		}

		return components;
	}

	/*
	 * get maps that define the scope of the hierarchies
	 */
	private HashMap<Class<?>, ArrayList<Component>> getScopeMap(boolean global)
	{
		if (global)
		{
			return descendantComponentMap;
		} else
		{
			return declaredComponentMap;
		}
	}

	/*
	 * This method creates the hierarchy structure used throughout the rest of
	 * the software. Using a combination of recursion and iteration through all
	 * superclasses it is possible to find almost all fields
	 */
	private void loadHierarchyComponents()
	{
		Object sysObj = self;
		Class superClass = sysObj.getClass();
		// System.out.println(self.getClass());
		ArrayList<Object> allFields = new ArrayList<Object>();
		{
			while (superClass != Object.class)
			{
				// System.out.println("Class " + superClass + " Object " +
				// sysObj.toString());
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
				// System.out.println("Class " + superClass);
				// System.out.println(superClass.getName());

			}
		}
		processFields((Component) sysObj, allFields);
	}

	/*
	 * Assign a parent and then traverses the system to construct its own
	 * hierarchy
	 */
	private void processComponent(Component parent, Component field)
	{
		field.getContents().parentComponent = parent;
		field.getContents().loadHierarchyComponents();
		parent.getContents().storeComponent(field, true);
	}

	/*
	 * Attempt to extract the contents from a container such as a hashmap or
	 * array list to be scannned
	 */
	private void processContainer(Component parent, Object container)
	{
		for (Object content : getContainerContents(container))
		{
			// System.out.println(content);
			processFields(parent, content);
		}
	}

	/*
	 * Processes an incoming field to determine what type of object it is
	 */
	private void processFields(Component parent, ArrayList<Object> fields)
	{
		for (Object field : fields)
		{
			processFields(parent, field);
		}
	}

	/*
	 * Process the object accordingly after its classification has been
	 * determined
	 */
	private void processFields(Component parent, Object field)
	{
		switch (getElementType(field))
		{
		case COMPONENT:
			processComponent(parent, (Component) field);
			break;
		case CONTAINER:
			// System.out.println(field.toString());
			processContainer(parent, field);
			break;
		default:
			break;
		}
	}

	/*
	 * Get a list of components of a certain class by searching through
	 * everything since items may get mapped by a sub or super class
	 */
	private <T> ArrayList<Component> scanForComponents(Class<T> scan_class, boolean global)
	{
		ArrayList<Component> foundObjects = new ArrayList<Component>();
		ArrayList<Component> allComponents = new ArrayList<Component>();
		if (global)
		{
			allComponents = descendantComponentList;
		} else
		{
			allComponents = declaredComponentList;
		}
		for (Component component : allComponents)
		{
			// System.out
			// .println(component.getClass().getSimpleName() + " " +
			// component.getClass().getInterfaces().length);
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

	/*
	 * Store a component once its location in the hierarchy has been determined
	 */
	private void storeComponent(Component component, boolean local)
	{
		if (component != null)
		{
			if (local)
			{
				if (!declaredComponentList.contains(component))
				{
					declaredComponentList.add(component);
				}
				if (!declaredComponentMap.containsKey(component.getClass()))
				{
					declaredComponentMap.put(component.getClass(), new ArrayList<Component>());// ..getProperties().getClassification()).add(allCurrent))));
				}
				if (!declaredComponentMap.get(component.getClass()).contains(component))
				{
					declaredComponentMap.get(component.getClass()).add(component);
				}
			}
			if (!descendantComponentList.contains(component))
			{
				descendantComponentList.add(component);
			}
			if (!descendantComponentMap.containsKey(component.getLabels().getClass()))
			{
				descendantComponentMap.put(component.getLabels().getClass(), new ArrayList<Component>());// ..getProperties().getClassification()).add(allCurrent))));
			}
			if (!descendantComponentMap.get(component.getLabels().getClass()).contains(component))
			{
				descendantComponentMap.get(component.getLabels().getClass()).add(component);
			}
		}
	}

	/*
	 * initialize necessary containers
	 */
	protected void initializeContainers()
	{
		descendantComponentMap = new HashMap<Class<?>, ArrayList<Component>>();
		declaredComponentMap = new HashMap<Class<?>, ArrayList<Component>>();
		declaredComponentList = new ArrayList<Component>();
		descendantComponentList = new ArrayList<Component>();
		declaredAdjunctComponentList = new ArrayList<Component>();
		declaredAdjunctDescendantComponentList = new ArrayList<Component>();
	}

	/*
	 * Environment key generated by environment that contains this component
	 */
	String getEnvironmentKey()
	{
		return environmentKey;
	}

	/*
	 * Change the environment key if need be
	 */
	void setEnvironmentKey(String environmentKey)
	{
		this.environmentKey = environmentKey;
	}

	/*
	 * Additional setup
	 */
	void setup()
	{
		initializeContainers();
		loadHierarchyComponents();
		// constructTree(this);
	}

	/*
	 * Constructor that defines the name and base class of the component
	 * 
	 * @param title - title of the component
	 * 
	 * @param base_class - base class of the component
	 */
	public ComponentOrganizer(Component self)
	{
		this.self = self;
		setup();
	}

	/*
	 * Extracts the objects out of many different types of containers
	 */
	public static ArrayList<Object> getContainerContents(Object container)
	{
		ArrayList<Object> components = new ArrayList<Object>();

		// if (!FieldFinder.containsSuper(container, CoreComponent.class))
		{
			try
			{
				for (Object entry : ((ArrayList) container))
				{
					components.add(entry);
				}

			} catch (Exception notMap)
			{
				// notMap.printStackTrace();
				try
				{
					for (Object entry : (HashMap.class.cast(container)).values())
					{
						components.add(entry);
					}
				} catch (Exception ee)
				{
					// ee.printStackTrace();
				}
			}
		}

		return components;

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

	/*
	 * Roughly categorize an object for sorting
	 */
	private static <T> ObjectType getElementType(T object)
	{
		ObjectType type = ObjectType.UNKNOWN;
		if (object != null)
		{
			if (FieldFinder.containsSuper(object, HashMap.class) || FieldFinder.containsSuper(object, ArrayList.class))
			{
				type = ObjectType.CONTAINER;
			} else if (FieldFinder.containsSuper(object, Component.class)
			|| object.getClass().getSuperclass().equals(Component.class))
			{
				type = ObjectType.COMPONENT;
			}
		}
		return type;
	}

	/*
	 * Constructs the actual hierarchy tree
	 */
	public void constructTree()
	{
		ComponentOrganizer hierarchy = self.getContents();
		hierarchy.loadHierarchyComponents();
		ArrayList<Component> init = new ArrayList<Component>();
		init.addAll(hierarchy.getComponents(true));
		for (Component component : init)
		{
			hierarchy.storeComponent(component, false);
			component.getContents().constructTree();// ComponentOrganizer.constructTree(component.getContents());
			for (Component componentChild : component.getContents().getComponents(true))
			{
				hierarchy.storeComponent(componentChild, false);
			}
		}

	}

	/*
	 * This enum is a very general way of categorizing items to speed up a
	 * variety of tasks
	 */
	private static enum ObjectType
	{
		COMPONENT,
		CONTAINER,
		JAVA,
		UNKNOWN;

	}
}
