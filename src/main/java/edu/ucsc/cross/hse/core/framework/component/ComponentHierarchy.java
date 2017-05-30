package edu.ucsc.cross.hse.core.framework.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.manipulation.ObjectCloner;

public class ComponentHierarchy
{

	public Component getParentComponent()
	{
		return parentComponent;
	}

	@CoreComponent
	public HashMap<Class<?>, ArrayList<Component>> childComponentMap;
	@CoreComponent
	public HashMap<Class<?>, ArrayList<Component>> descendantComponentMap;
	@CoreComponent
	private ArrayList<Component> childComponents; // direct children of this
													// component (no childrens
													// children)
	@CoreComponent
	private ArrayList<Component> descendantComponents; // all children of this
														// component (all
														// children & childrens
														// children)

	@CoreComponent
	private Component parentComponent; // parent component
	@CoreComponent
	private Component self; // parent component

	/*
	 * Constructor that defines the name and base class of the component
	 * 
	 * @param title - title of the component
	 * 
	 * @param base_class - base class of the component
	 */
	public ComponentHierarchy(Component self)
	{
		this.self = self;
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
			initialClone = clonedComponent;
		}
	}

	private HashMap<Class<?>, ArrayList<Component>> getScopeMap(boolean global)
	{
		if (global)
		{
			return descendantComponentMap;
		} else
		{
			return childComponentMap;
		}
	}

	protected void initializeContainers()
	{
		descendantComponentMap = new HashMap<Class<?>, ArrayList<Component>>();
		childComponentMap = new HashMap<Class<?>, ArrayList<Component>>();
		childComponents = new ArrayList<Component>();
		descendantComponents = new ArrayList<Component>();
	}

	private void processComponent(Component parent, Component field)
	{
		field.getHierarchy().parentComponent = parent;
		field.getHierarchy().loadHierarchyComponents();
		parent.getHierarchy().storeComponent(field, true);
	}

	private void processContainer(Component parent, Object container)
	{
		for (Object content : getContainerContents(container))
		{
			// System.out.println(content);
			processFields(parent, content);
		}
	}

	private void processFields(Component parent, ArrayList<Object> fields)
	{
		for (Object field : fields)
		{
			processFields(parent, field);
		}
	}

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

	private void setup()
	{
		initializeContainers();
	}

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

	private void storeComponent(Component component, boolean local)
	{
		if (component != null)
		{
			if (local)
			{
				if (!childComponents.contains(component))
				{
					childComponents.add(component);
				}
				if (!childComponentMap.containsKey(component.getProperties().getBaseComponentClass()))
				{
					childComponentMap.put(component.getProperties().getBaseComponentClass(),
					new ArrayList<Component>());// ..getProperties().getClassification()).add(allCurrent))));
				}
				if (!childComponentMap.get(component.getProperties().getBaseComponentClass()).contains(component))
				{
					childComponentMap.get(component.getProperties().getBaseComponentClass()).add(component);
				}
			}
			if (!descendantComponents.contains(component))
			{
				descendantComponents.add(component);
			}
			if (!descendantComponentMap.containsKey(component.getProperties().getBaseComponentClass()))
			{
				descendantComponentMap.put(component.getProperties().getBaseComponentClass(),
				new ArrayList<Component>());// ..getProperties().getClassification()).add(allCurrent))));
			}
			if (!descendantComponentMap.get(component.getProperties().getBaseComponentClass()).contains(component))
			{
				descendantComponentMap.get(component.getProperties().getBaseComponentClass()).add(component);
			}
		}
	}

	public static ArrayList<Object> getContainerContents(Object container)
	{
		ArrayList<Object> components = new ArrayList<Object>();

		if (!FieldFinder.containsSuper(container, CoreComponent.class))
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

	//
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

	public static void constructTree(ComponentHierarchy hierarchy)
	{
		hierarchy.loadHierarchyComponents();
		ArrayList<Component> init = new ArrayList<Component>();
		init.addAll(hierarchy.getComponents(true));
		for (Component component : init)
		{
			ComponentHierarchy.constructTree(component.getHierarchy());
			for (Component componentChild : component.getComponents(true))
			{
				hierarchy.storeComponent(componentChild, false);
			}
		}

	}

	private static enum ObjectType
	{
		COMPONENT,
		CONTAINER,
		JAVA,
		UNKNOWN;

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

	public ArrayList<Component> getComponents(boolean include_children, Class... classes)
	{
		return getComponents(Component.class, include_children, classes);
	}

	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> getComponents(Class<T> component_class, boolean include_children, Class... classes)
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
				componentz = getComponents(component_class, include_children, clas);
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
					childComponentMap.put(clas, componente);
				}
				allComponents.addAll((ArrayList<T>) componente);
				// components = getComponents(component_class,
				// include_children);
			}
		}
		return allComponents;
	}

	@SuppressWarnings("unchecked")
	private <T> ArrayList<T> getComponents(Class<T> component_class, boolean include_children, Class<?> search_class)
	{
		ArrayList<T> components = new ArrayList<T>();
		try
		{
			if (include_children)
			{
				components = (ArrayList<T>) descendantComponentMap.get(search_class);
			} else
			{
				components = (ArrayList<T>) childComponentMap.get(search_class);
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
				childComponentMap.put(search_class, componentz);
			}
			components = (ArrayList<T>) componentz;
			// components = getComponents(component_class, include_children);
		}

		return components;
	}

}
