package edu.ucsc.cross.hse.core.processing.execution;

import java.io.File;
import java.util.HashMap;

import edu.ucsc.cross.hse.core.framework.component.ComponentConfigurer;
import edu.ucsc.cross.hse.core.framework.component.ComponentContent;
import edu.ucsc.cross.hse.core.framework.environment.HybridEnvironment;
import edu.ucsc.cross.hse.core.procesing.io.FileContent;
import edu.ucsc.cross.hse.core.procesing.io.SystemConsole;
import edu.ucsc.cross.hse.core.processing.data.DataAccessor;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;

public class EnvironmentManager
{

	static HashMap<String, EnvironmentManager> environments = new HashMap<String, EnvironmentManager>();

	protected HybridEnvironment content; // all elements that make up the

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
	public EnvironmentManager()
	{

		initialize(new HybridEnvironment(), false);
	}

	/*
	 * Named environment constructor that initializes an empty system with a
	 * specified name and default settings
	 */
	public EnvironmentManager(String name)
	{
		initialize(new HybridEnvironment(name), false);
	}

	/*
	 * Predefined environment constructor that loads a specified environment and
	 * default settings
	 */
	public EnvironmentManager(HybridEnvironment environment)
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
		processor.startEnvironment();
	}

	/*
	 * Stop the environment
	 */
	public void stop()
	{
		processor.interruptResponder.killEnv();
		processor.systemConsole.print("Environment Stopped - Simulation Time: " + getEnvironment().getEnvironmentTime()
		+ " sec - Run Time : " + processor.integrationMonitor.getRunTime() + "sec");
	}

	/*
	 * Pause the environment
	 */
	public void pause()
	{
		processor.interruptResponder.pauseEnv();
		processor.systemConsole.print("Environment Paused - Simulation Time: " + getEnvironment().getEnvironmentTime()
		+ " sec - Run Time : " + processor.integrationMonitor.getRunTime() + "sec");
	}

	/*
	 * Resume the environment from a pause
	 */
	public void resume()
	{
		processor.systemConsole
		.print("Environment Resumed - Simulation Time: " + getEnvironment().getEnvironmentTime() + " sec");
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
	 * Reloads all data present within the environment
	 */
	public void refresh()
	{
		CentralProcessor.refreshIfDataPresent(this, content);
	}

	/*
	 * Clear the contents of the environment
	 */
	public void clear()
	{
		clear(false);
	}

	/*
	 * Clear the contents of the environment
	 */
	public void clear(boolean clear_settings)
	{
		initialize(new HybridEnvironment(content.component().getLabels().getName()), false, clear_settings);
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
	public HybridEnvironment getEnvironment()
	{
		return content;
	}

	/*
	 * Access the environment contents
	 */
	public ComponentContent getContent()
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
	 * Access the system console
	 */
	public SystemConsole console()
	{
		return processor.systemConsole;
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
		FileContent[] contents = FileContent.getContentArray(save_data, save_settings);
		processor.fileExchanger.store(file, content, contents);
	}

	/*
	 * Load a different set of contents
	 */
	public void load(HybridEnvironment content)
	{
		this.content = content;
		processor.prepareEnvironment(content);
	}

	/*
	 * Load specified contents from a file based on input flags
	 */
	public void load(File file, boolean load_data, boolean load_settings)
	{
		FileContent[] content = FileContent.getContentArray(load_data, load_settings);
		load((HybridEnvironment) processor.fileExchanger.load(file, content));
	}

	/*
	 * Load contents from a file including data and settings
	 */
	public void load(File file)
	{
		load((HybridEnvironment) processor.fileExchanger.load(file, FileContent.values()));
	}

	/*
	 * Get the current settings
	 */
	public SettingConfigurer getSettings()
	{
		return settings;
	}

	/*
	 * Access this environment statically using the key generated by the content
	 */
	public static EnvironmentManager getEnvironment(String content_id)
	{
		if (environments.containsKey(content_id))
		{
			return environments.get(content_id);
		} else
		{
			return null;
		}
	}

	/*
	 * initialize the environment content, specifying if content contains data
	 */
	private void initialize(HybridEnvironment content, boolean pre_loaded_content)
	{
		initialize(content, pre_loaded_content, true);
	}

	/*
	 * initialize the environment content, specifying if content contains data
	 * and if settings should be reinitialized
	 */
	private void initialize(HybridEnvironment content, boolean pre_loaded_content, boolean re_initialize_settings)
	{
		this.content = content;
		environments.put(this.toString(), this);
		if (re_initialize_settings)
		{
			settings = new SettingConfigurer();
			settings.loadSettingsFromXMLFile(null);
		}
		if (processor == null)
		{
			processor = new CentralProcessor(this);
		} else
		{
			// processor.initializeProcessingElements();
		}
		if (pre_loaded_content)
		{
			processor.dataHandler.loadStoreStates();
		}
	}
}
