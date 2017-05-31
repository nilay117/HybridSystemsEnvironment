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
import edu.ucsc.cross.hse.core.framework.models.DynamicalModel;

public class ComponentOperator extends ComponentActions
{

	protected static HashMap<Component, ComponentOperator> components = new HashMap<Component, ComponentOperator>();

	// public Component component;

	public ComponentOperator(Component component)
	{
		super(component);
		// this.component = component;
		components.put(component, this);
		// getConfigurer(component);
		// component.hierarchy.
	}

	@CoreComponent
	public static Cloner cloner = new Cloner();

	public void setEnvironment(String environment_id)
	{
		component.address().setEnvironmentKey(environment_id);
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

	public void saveComponentToFile(String directory_path, String file_name)
	{
		Object clonedComponent = ObjectCloner.xmlClone(this);

		FileSystemOperator.createOutputFile(new File(directory_path, file_name), XMLParser.serializeObject(this));// clonedComponent));

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

	public Boolean isInitialized()
	{
		return component.configuration().getInitialized();
	}

	public void setInitialized(Boolean initialized)
	{
		component.configuration().setInitialized(initialized);
	}

	protected void resetHierarchy()
	{
		component.getHierarchy().setup();
	}

	public String getEnvironmentKey()
	{
		return component.address().getEnvironmentKey();
	}

	public void protectedInitialize()
	{
		if (!component.configuration().getInitialized())
		{
			component.initialize();
			component.configuration().setInitialized(true);
		}
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

	public boolean isSimulated()
	{
		return component.configuration().isSimulated();
	}

	public void setSimulated(boolean simulated)
	{
		component.configuration().setSimulated(simulated);
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
				boolean jumpOccurred = DynamicalModel.applyDynamics(localBehavior, true, jump_occurring);
				if (jumpOccurred)
				{
					component.getEnvironment().getEnvironmentTime().incrementJumpIndex();
				}
			} catch (Exception behaviorFail)
			{
				behaviorFail.printStackTrace();
			}
		}
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
						jumpComponents.add(localBehavior);
					}
				}
			} catch (Exception behaviorFail)
			{
				behaviorFail.printStackTrace();
			}
		}
		return jumpComponents;
	}
}
