package edu.ucsc.cross.hse.core.component.foundation;

import java.io.File;
import java.util.HashMap;

import com.rits.cloning.Cloner;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.component.constructors.Component;

public class ComponentConfigurer
{

	private static HashMap<Component, ComponentConfigurer> components = new HashMap<Component, ComponentConfigurer>();

	public Component component;

	public ComponentConfigurer(Component component)
	{
		this.component = component;
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

	public static ComponentConfigurer getConfigurer(Component component)
	{
		if (components.containsKey(component))
		{
			return components.get(component);

		} else
		{

			ComponentConfigurer config = new ComponentConfigurer(component);
			components.put(component, config);
			return config;

		}
	}
	// public GlobalHybridSystem getEnvironment()
	// {
	// return environment;
	// }
}
