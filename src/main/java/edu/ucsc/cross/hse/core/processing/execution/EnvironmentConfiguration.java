package edu.ucsc.cross.hse.core.processing.execution;

import java.io.File;
import java.util.HashMap;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentConfigurer;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.procesing.io.FileContent;
import edu.ucsc.cross.hse.core.procesing.io.FileProcessor;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;

public class EnvironmentConfiguration extends ComponentConfigurer
{

	private HybridEnvironment env; // wpointer to own component

	protected static HashMap<HybridEnvironment, EnvironmentConfiguration> components = new HashMap<HybridEnvironment, EnvironmentConfiguration>();

	public EnvironmentConfiguration(HybridEnvironment environment)
	{
		super(environment.content);
		env = environment;
	}

	/*
	 * Load a different set of contents
	 */
	public void loadContents(EnvironmentContent content)
	{
		env.processor.prepareEnvironment(content);
	}

	/*
	 * Load contents from a file
	 */
	public void loadContentsFromFile(File file, boolean load_data, boolean load_settings)
	{
		FileContent[] content = FileProcessor.getContentArray(load_data, load_settings);
		loadContents((EnvironmentContent) env.processor.fileExchanger.load(file, content));
	}

	public void loadContentsFromFile(File file)
	{
		env.processor.fileExchanger.load(file, FileContent.values());
	}

	/*
	 * Get the current settings
	 */
	public SettingConfigurer getSettings()
	{
		return env.settings;
	}

	/*
	 * Indirect access to the operator to keep it disconnected from the
	 * component itself
	 */
	public static EnvironmentConfiguration getOperator(HybridEnvironment component)
	{
		if (components.containsKey(component))
		{
			return components.get(component);

		} else
		{

			EnvironmentConfiguration config = new EnvironmentConfiguration(component);
			components.put(component, config);
			return config;

		}
	}
}
