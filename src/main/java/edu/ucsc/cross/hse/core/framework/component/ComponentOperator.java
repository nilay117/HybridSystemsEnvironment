package edu.ucsc.cross.hse.core.framework.component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.rits.cloning.Cloner;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystemOperator;
import edu.ucsc.cross.hse.core.framework.models.DynamicalModel;

/*
 * This class contains the methods that are used by processing modules to
 * operate the component. These methods are not intended for use outside of
 * processing, which is why they are not directly accessable from the component
 * itself. Any additional methods that should not be accessible by users should
 * be defined here. Use caution when using them as they can disrupt
 * functionality of the environment.
 */
public class ComponentOperator extends ComponentActions
{

	public static Cloner cloner = new Cloner();

	// public Component component;

	protected static HashMap<Component, ComponentOperator> components = new HashMap<Component, ComponentOperator>();

	public ComponentOperator(Component component)
	{
		super(component);
		// this.component = component;
		components.put(component, this);
		// getConfigurer(component);
		// component.hierarchy.
	}

	public Component addComponentFromFile(String file_path)
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

	public String getEnvironmentKey()
	{
		return component.getHierarchy().getEnvironmentKey();
	}

	public boolean getIfData()
	{
		try
		{
			Data d = Data.class.cast(component);
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}

	public ProtectedComponentData getStatus()
	{
		return component.getStatus();
	}

	public Boolean isInitialized()
	{
		return component.getStatus().getInitialized();
	}

	public boolean isSimulated()
	{
		return component.getStatus().isSimulated();
	}

	/*
	 * Determines whether or not a jump is occurring in any component within the
	 * hybrid system
	 * 
	 * @return true if a jump is occurring, false otherwise
	 */
	public ArrayList<Component> jumpingComponents()
	{
		ArrayList<Component> jumpComponents = new ArrayList<Component>();
		// System.out.println(getEnvironment().getMatchingComponents(Component.class,
		// true));
		for (Component localBehavior : component.getHierarchy().getComponents(Component.class, true,
		DynamicalModel.class))// ,
		// DynamicalModel.class))
		{
			try
			{
				Boolean jumpOccurring = DynamicalModel.jumpOccurring((DynamicalModel) localBehavior, true);
				if (jumpOccurring != null)
				{
					if (jumpOccurring)
					{
						if (!jumpComponents.contains(localBehavior)) // make
																		// sure
																		// the
																		// dynamical
																		// model
																		// has
																		// not
																		// been
																		// accounted
																		// for
																		// already
						{
							jumpComponents.add(localBehavior);
						}
					}
				}
			} catch (Exception behaviorFail)
			{
				behaviorFail.printStackTrace();
			}
		}
		return jumpComponents;
	}

	/*
	 * Determines whether or not a jump is occurring in any component within the
	 * hybrid system
	 * 
	 * @return true if a jump is occurring, false otherwise
	 */
	public Boolean jumpOccurring()
	{
		Boolean jumpOccurred = false;
		for (DynamicalModel localBehavior : component.getHierarchy().getComponents(DynamicalModel.class, true))
		{
			try
			{
				Boolean jumpOccurring = DynamicalModel.jumpOccurring(localBehavior, true);
				if (jumpOccurring != null)
				{
					try
					{
						jumpOccurred = jumpOccurred || jumpOccurring;
					} catch (Exception outOfDomain)
					{
						outOfDomain.printStackTrace();
					}
				}
			} catch (Exception behaviorFail)
			{
				behaviorFail.printStackTrace();
			}
		}
		return jumpOccurred;
	}

	/*
	 * Performs all sub component tasks according to the current domain (jump,
	 * flow, or neither)
	 * 
	 * @param jump_occurring
	 */
	public void performTasks(boolean jump_occurring)
	{

		for (DynamicalModel localBehavior : component.getHierarchy().getComponents(DynamicalModel.class, true))
		{
			try
			{

				boolean jumpOccurred = false;
				if (DynamicalModel.jumpOccurring(localBehavior, true))
				{
					jumpOccurred = DynamicalModel.applyDynamics(localBehavior, true, jump_occurring);
				} else if (DynamicalModel.flowOccurring(localBehavior, true))
				{
					jumpOccurred = DynamicalModel.applyDynamics(localBehavior, true, jump_occurring);
				} else
				{

				}
				if (jumpOccurred)
				{
					GlobalSystemOperator.getGlobalSystemOperator(getEnvironmentKey()).getEnvironmentHybridTime()
					.incrementJumpIndex();
				}
			} catch (Exception behaviorFail)
			{
				behaviorFail.printStackTrace();
			}
		}
	}

	public void protectedInitialize()
	{
		if (!component.getStatus().getInitialized())
		{
			component.initialize();
			component.getStatus().setInitialized(true);
		}
	}

	public void resetHierarchy()
	{
		component.getHierarchy().setup();
	}

	public void saveComponentToFile(String directory_path, String file_name)
	{
		Object clonedComponent = ObjectCloner.xmlClone(this.component);

		FileSystemOperator.createOutputFile(new File(directory_path, file_name),
		XMLParser.serializeObject(this.component));// clonedComponent));

	}

	public void setEnvironmentKey(String environment_key)
	{
		component.getHierarchy().setEnvironmentKey(environment_key);
	}

	public void setInitialized(Boolean initialized)
	{
		component.getStatus().setInitialized(initialized);
	}

	public void setSimulated(boolean simulated)
	{
		component.getStatus().setSimulated(simulated);
	}

	public void uninitializeComponent()
	{
		ComponentHierarchy.constructTree(component.getHierarchy());
		for (Component comp : component.getHierarchy().getComponents(true))
		{
			ComponentOperator.getConfigurer(comp).setInitialized(false);
		}
	}

	public static ComponentOperator getConfigurer(Component component)
	{
		if (components.containsKey(component))
		{
			return components.get(component);

		} else
		{

			ComponentOperator config = new ComponentOperator(component);
			components.put(component, config);
			return config;

		}
	}
}
