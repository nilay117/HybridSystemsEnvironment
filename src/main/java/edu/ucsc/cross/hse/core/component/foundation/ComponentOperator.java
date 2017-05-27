package edu.ucsc.cross.hse.core.component.foundation;

import java.io.File;
import java.util.HashMap;

import com.rits.cloning.Cloner;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.component.data.Data;
import edu.ucsc.cross.hse.core.object.accessors.Hierarchy;

public class ComponentOperator
{

	protected static HashMap<Component, ComponentOperator> components = new HashMap<Component, ComponentOperator>();

	public Component component;

	public ComponentOperator(Component component)
	{
		this.component = component;
		components.put(component, this);
		// getConfigurer(component);
		// component.hierarchy.
	}

	@CoreComponent
	public static Cloner cloner = new Cloner();

	public void setEnvironment(String environment_id)
	{
		component.environmentKey = environment_id;
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
		return component.initialized;
	}

	public void setInitialized(Boolean initialized)
	{
		component.initialized = initialized;
	}

	protected void resetHierarchy()
	{
		component.hierarchy = new Hierarchy(component);
	}

	public String getEnvironmentKey()
	{
		return component.environmentKey;
	}

	public void protectedInitialize()
	{
		if (!component.initialized)
		{
			component.initialize();
			component.initialized = (true);
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
		return component.simulated;
	}

	public void setSimulated(boolean simulated)
	{
		component.simulated = simulated;
	}

}
