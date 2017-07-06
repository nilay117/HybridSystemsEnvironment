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

import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOrganizer;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.framework.environment.ContentOperator;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.procesing.io.FileContent;
import edu.ucsc.cross.hse.core.procesing.io.FileExchanger;
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
	SettingConfigurer settings; // collection of settings that
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
		processor.startEnvironment();
	}

	/*
	 * Stop the environment
	 */
	public void stop()
	{
		processor.interruptResponder.killEnv();
		processor.systemConsole.print("Environment Stopped - Simulation Time: " + getContents().getEnvironmentTime()
		+ " sec - Run Time : " + processor.integrationMonitor.getRunTime() + "sec");
	}

	/*
	 * Pause the environment
	 */
	public void pause()
	{
		processor.interruptResponder.pauseSim();
		processor.systemConsole.print("Environment Paused - Simulation Time: " + getContents().getEnvironmentTime()
		+ " sec - Run Time : " + processor.integrationMonitor.getRunTime() + "sec");
	}

	/*
	 * Resume the environment from a pause
	 */
	public void resume()
	{
		processor.systemConsole
		.print("Environment Resumed - Simulation Time: " + getContents().getEnvironmentTime() + " sec");
		processor.startEnvironment(true);

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

	/*
	 * Clear the contents of the environment
	 */
	public void clear()
	{
		initialize(new EnvironmentContent(content.getLabels().getName()), false);
	}

	/*
	 * Access the data organization module
	 */
	public DataAccessor getDataAccessor()
	{
		return processor.dataHandler;
	}

	/*
	 * Access the environment contents
	 */
	public EnvironmentContent getContents()
	{
		return content;
	}

	/*
	 * Load a different set of contents
	 */
	public void loadContents(EnvironmentContent content)
	{

		processor.prepareEnvironment(content);
	}

	/*
	 * Load contents from a file
	 */
	public void loadContentsFromFile(File file)
	{

		this.processor.fileExchanger.load(file);
		//		processor.prepareEnvironment(content);
		//		processor.storeConfigurations();
		//		processor.contentAdmin = ContentOperator.getOperator(content);
		//		this.processor.dataHandler.loadStoreStates();
	}

	/*
	 * Save the contents of the environment to a file
	 */
	public void saveContents(File file)
	{
		this.processor.fileExchanger.storeEnvironment(file);
	}

	/*
	 * Get the current settings
	 */
	public SettingConfigurer getSettings()
	{
		return settings;
	}

	/*
	 * Load new setting components from files
	 */
	public void loadSettings(Object... settings)
	{
		if (settings.length == 1)
		{
			if (settings.getClass().equals(SettingConfigurer.class))
			{
				this.settings = (SettingConfigurer) settings[0];
			}
		}
	}

	/*
	 * Load settings from a file
	 */
	public void loadSettingsFromFile(File file)
	{
		processor.fileExchanger.load(file, false);
	}

	/*
	 * Save the settings to a file
	 */
	public void saveSettings(File file)
	{
		processor.fileExchanger.store(file, this.content, FileContent.SETTINGS);
	}

	/*
	 * Add components to the environment
	 */
	public void addComponents(Component... components)
	{
		for (Component component : components)
		{
			content.getContents().addComponent(component);
		}
	}

	/*
	 * Add components to the environment in bulk
	 */
	public void addComponents(Component component, Integer quantity)
	{

		content.getContents().addComponent(component, quantity);

	}

	/*
	 * Load contents from a file
	 */
	public void loadComponentFromFile(File file)
	{
		loadComponentsFromFile(file, 1);
	}

	/*
	 * Load contents from a file
	 */
	public void loadComponentsFromFile(File file, Integer quantity)
	{

		Component component = this.processor.fileExchanger.load(file, false);
		addComponents(component, quantity);
		try
		{
			ArrayList<Data> datas = new ArrayList<Data>();
			datas.addAll(component.getContents().getObjects(State.class, true));
			datas.addAll(component.getContents().getObjects(Data.class, true));
			boolean loadData = false;
			for (Data data : datas)
			{
				if (data.getActions().getStoredValues().size() > 0)
				{
					loadData = true;
					break;
				}
			}
			if (loadData)
			{
				processor = new CentralProcessor(this);
				processor.prepareEnvironment(getContents());
			}
		} catch (Exception noStates)
		{
			noStates.printStackTrace();
		}

	}

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
		environments.put(this.toString(), this);
		settings = FileExchanger.loadSettings(null);
		processor = new CentralProcessor(this);
		if (pre_loaded_content)
		{
			processor.dataHandler.loadStoreStates();
		}
	}
}
