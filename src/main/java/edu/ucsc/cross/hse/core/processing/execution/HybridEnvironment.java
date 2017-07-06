package edu.ucsc.cross.hse.core.processing.execution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

import bs.commons.objects.manipulation.XMLParser;
import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentConfigurer;
import edu.ucsc.cross.hse.core.framework.component.ComponentOrganizer;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.framework.environment.ContentOperator;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.procesing.io.FileContent;
import edu.ucsc.cross.hse.core.procesing.io.FileProcessor;
import edu.ucsc.cross.hse.core.processing.data.DataAccessor;
import edu.ucsc.cross.hse.core.processing.data.DataHandler;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;

public class HybridEnvironment// implements Serializable
{

	static HashMap<String, HybridEnvironment> environments = new HashMap<String, HybridEnvironment>();

	protected EnvironmentContent content; // all elements that make up the
	// environment
	// itself such as data, components, systems
	// etc
	protected CentralProcessor processor; // environment processor that handles
											// events,
											// computations, maintenance, etc
	protected SettingConfigurer settings; // collection of settings that
	// configure the performance of the
	// environment

	/*
	 * Generic environment constructor that initializes an empty system with a
	 * default name and settings
	 */
	public HybridEnvironment()
	{

		initialize(new EnvironmentContent(), false);
	}

	/*
	 * Named environment constructor that initializes an empty system with a
	 * specified name and default settings
	 */
	public HybridEnvironment(String name)
	{
		initialize(new EnvironmentContent(name), false);
	}

	/*
	 * Predefined environment constructor that loads a specified environment and
	 * default settings
	 */
	public HybridEnvironment(EnvironmentContent environment)
	{
		this.content = environment;
		initialize(environment, true);
	}

	/*
	 * Run the environment for the amount of time defined in the settings
	 */
	public void start()
	{
		start(settings.getExecutionSettings().simDuration);
	}

	/*
	 * Run the environment for the specified amount of time
	 */
	public void start(Double duration)
	{
		processor.systemConsole.print("Environment Started");
		settings.getExecutionSettings().simDuration = duration;
		processor.launchEnvironment();
	}

	/*
	 * Stop the environment
	 */
	public void stop()
	{
		processor.interruptResponder.killEnv();
		processor.systemConsole.print("Environment Stopped - Simulation Time: " + getE().getEnvironmentTime()
		+ " sec - Run Time : " + processor.integrationMonitor.getRunTime() + "sec");
	}

	/*
	 * Pause the environment
	 */
	public void pause()
	{
		processor.interruptResponder.pauseSim();
		processor.systemConsole.print("Environment Paused - Simulation Time: " + getE().getEnvironmentTime()
		+ " sec - Run Time : " + processor.integrationMonitor.getRunTime() + "sec");
	}

	/*
	 * Resume the environment from a pause
	 */
	public void resume()
	{
		processor.systemConsole.print("Environment Resumed - Simulation Time: " + getE().getEnvironmentTime() + " sec");
		processor.launchEnvironment(true);
	}

	/*
	 * Reset all data and variables back to their original states
	 */
	public void reset()
	{
		reset(false);
	}

	/*
	 * Reset all data and variables back to their original states and
	 * re-initialize
	 */
	public void reset(boolean re_initialize)
	{
		processor.systemConsole.print("Environment Reset - Components Re-Initialized: " + re_initialize);
		processor.resetEnvironment(re_initialize);
	}

	public void refresh()
	{
		CentralProcessor.refreshIfDataPresent(this, content);
	}

	/*
	 * Clear the contents of the environment
	 */
	public void clear()
	{
		initialize(new EnvironmentContent(content.component().getLabels().getName()), false);
	}

	/*
	 * Access the data organization module
	 */
	public DataAccessor getData()
	{
		return processor.dataHandler;
	}

	/*
	 * Access the environment contents
	 */
	public EnvironmentContent getE()
	{
		return content;
	}

	/*
	 * Access the environment contents
	 */
	public ComponentOrganizer getContent()
	{
		return content.component().getContent();
	}

	/*
	 * Access the environment contents
	 */
	public ComponentConfigurer configure()
	{
		return ComponentConfigurer.getOperator(content);
	}

	/*
	 * Save the contents of the environment to a file
	 */
	public void save(File file)
	{
		save(file, true, true);
	}

	/*
	 * Save the contents of the environment to a file
	 */
	public void save(File file, boolean save_data, boolean save_settings)
	{
		FileContent[] contents = FileProcessor.getContentArray(save_data, save_settings);
		processor.fileExchanger.store(file, content, contents);
	}

	/*
	 * Load a different set of contents
	 */
	public void load(EnvironmentContent content)
	{
		this.content = content;
		processor.prepareEnvironment(content);
	}

	/*
	 * Load contents from a file
	 */
	public void load(File file, boolean load_data, boolean load_settings)
	{
		FileContent[] content = FileProcessor.getContentArray(load_data, load_settings);
		load((EnvironmentContent) processor.fileExchanger.load(file, content));
	}

	public void load(File file)
	{
		load((EnvironmentContent) processor.fileExchanger.load(file, FileContent.values()));
	}

	/*
	 * Get the current settings
	 */
	public SettingConfigurer getSettings()
	{
		return settings;
	}
	//
	// /*
	// * Load a different set of contents
	// */
	// public void loadContents(EnvironmentContent content)
	// {
	//
	// processor.prepareEnvironment(content);
	// }
	//
	// /*
	// * Load contents from a file
	// */
	// public void loadContentsFromFile(File file, boolean load_data, boolean
	// load_settings)
	// {
	// FileContent[] content = FileProcessor.getContentArray(load_data,
	// load_settings);
	// loadContents((EnvironmentContent) processor.fileExchanger.load(file,
	// content));
	// }
	//
	// public void loadContentsFromFile(File file)
	// {
	// processor.fileExchanger.load(file, FileContent.values());
	// }
	//
	// /*
	// * Save the contents of the environment to a file
	// */
	// public void saveEnvironment(File file)
	// {
	// saveEnvironment(file, true, true);
	// }
	//
	// /*
	// * Save the contents of the environment to a file
	// */
	// public void saveEnvironment(File file, boolean save_data, boolean
	// save_settings)
	// {
	// FileContent[] contents = FileProcessor.getContentArray(save_data,
	// save_settings);
	// this.processor.fileExchanger.store(file, this.content, contents);
	// }
	//
	// /*
	// * Get the current settings
	// */
	// public SettingConfigurer getSettings()
	// {
	// return settings;
	// }
	//
	// /*
	// * Load settings from a file
	// */
	// public void loadSettingsFromXMLFile(File file)
	// {
	//
	// SettingConfigurer loaded = null;
	// if (file == null)
	// {
	// loaded = FileProcessor.loadXMLSettings();
	// } else
	// {
	// loaded = FileProcessor.loadXMLSettings(file);
	// }
	// for (Object set : SettingConfigurer.getSettingsMap(loaded).values())
	// {
	// settings.loadSettings(set);
	// }
	// }
	//
	// /*
	// * Save the settings to a file
	// */
	// public void saveSettingsToXMLFile(File file)
	// {
	// processor.fileExchanger.store(file, this.content, FileContent.SETTINGS);
	// }

	// /*
	// * Add components to the environment
	// */
	// public void addComponents(Component... components)
	// {
	// for (Component component : components)
	// {
	// addComponents(component, 1);
	// }
	// }
	//
	// /*
	// * Add components to the environment in bulk
	// */
	// public void addComponents(Component component, Integer quantity)
	// {
	//
	// content.getConfiguration().addComponent(component, quantity);
	// }
	//
	// /*
	// * Load contents from a file
	// */
	// public void loadComponentFromFile(File file)
	// {
	// loadComponentsFromFile(file, 1);
	// }
	//
	// /*
	// * Load contents from a file
	// */
	// public void loadComponentsFromFile(File file, Integer quantity)
	// {
	//
	// Component component = this.processor.fileExchanger.load(file,
	// FileContent.COMPONENT, FileContent.DATA);
	// addComponents(component, quantity);
	//
	// }

	// public EnvironmentConfiguration getActions()
	// {
	// return EnvironmentConfiguration.getOperator(this);
	// }

	/*
	 * Access this environment statically using the key generated by the content
	 */
	public static HybridEnvironment getEnvironment(String content_id)
	{
		if (environments.containsKey(content_id))
		{
			return environments.get(content_id);
		} else
		{
			return null;
		}
	}

	private void initialize(EnvironmentContent content, boolean pre_loaded_content)
	{
		this.content = content;
		settings = new SettingConfigurer();
		environments.put(this.toString(), this);
		settings.loadSettingsFromXMLFile(null);
		processor = new CentralProcessor(this);
		if (pre_loaded_content)
		{
			processor.dataHandler.loadStoreStates();
		}
	}
}
