package edu.ucsc.cross.hse.core.processing.data;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

import com.be3short.data.file.general.FileSystemInteractor;
import com.be3short.data.file.xml.XMLParser;
import edu.ucsc.cross.hse.core.io.file.FileContent;
import edu.ucsc.cross.hse.core.io.file.FileProcessor;
import edu.ucsc.cross.hse.core.object.configuration.ComputationSettings;
import edu.ucsc.cross.hse.core.object.configuration.ConsoleSettings;
import edu.ucsc.cross.hse.core.object.configuration.DataSettings;
import edu.ucsc.cross.hse.core.object.configuration.ExecutionSettings;

/*
 * Accesses and configurers the setting files, and allows for additional custom
 * settings to be included.
 */
public class SettingConfigurer
{

	/*
	 * Mapping of all setting components
	 */
	private HashMap<Class<?>, Object> settings;

	/*
	 * Constructor for default settings
	 */
	public SettingConfigurer()
	{
		initialize();
	}

	/*
	 * initialize default settings and mapping
	 */
	private void initialize()
	{
		settings = new HashMap<Class<?>, Object>();
		settings.put(ConsoleSettings.class, new ConsoleSettings());
		settings.put(ComputationSettings.class, new ComputationSettings());
		settings.put(DataSettings.class, new DataSettings());
		settings.put(ExecutionSettings.class, new ExecutionSettings());
	}

	public ComputationSettings getComputationSettings()
	{
		// return computation;
		return (ComputationSettings) settings.get(ComputationSettings.class);
	}

	/*
	 * Gets console print settings
	 * 
	 * @return console print settings
	 */
	public ConsoleSettings getConsolePrintSettings()
	{
		return (ConsoleSettings) settings.get(ConsoleSettings.class);
	}

	/*
	 * Gets data settings
	 * 
	 * @return data settings
	 */
	public DataSettings getDataSettings()
	{
		return (DataSettings) settings.get(DataSettings.class);
		// return data;
	}

	/*
	 * Gets execution settings
	 * 
	 * @return execution settings
	 */
	public ExecutionSettings getExecutionSettings()
	{
		return (ExecutionSettings) settings.get(ExecutionSettings.class);
		// return trial;
	}

	/*
	 * Load any type of settings for use within the environment
	 */
	public void loadSettings(Object... setting)
	{
		for (Object settinG : setting)
		{
			settings.put(settinG.getClass(), settinG);
		}
	}

	/*
	 * Store new setting configuration components
	 * 
	 * @param setting - settings configuration to be loaded from
	 */
	public void setSettings(SettingConfigurer setting)
	{
		for (Object set : SettingConfigurer.getSettingsMap(setting).values())
		{
			loadSettings(set);
		}
	}

	/*
	 * Get a setting component corresponding to specified class
	 * 
	 * @return setting component
	 */
	public <T> T getSetting(Class<T> setting_class)
	{
		T returnSetting = null;
		if (settings.containsKey(setting_class))
		{
			returnSetting = setting_class.cast(settings.get(setting_class));
		}
		return returnSetting;
	}

	/*
	 * Load settings from a file
	 */
	public void loadSettingsFromFile(String file_path)
	{

		loadSettingsFromFile(new File(file_path));
	}

	/*
	 * Load settings from a file
	 */
	public void loadSettingsFromFile(File file)
	{
		if (file == null)
		{
			SettingConfigurer.loadDefaultSettingsFile();
		} else if (file.exists())
		{
			if (file.getName().contains(".xml"))
			{
				SettingConfigurer loaded = SettingConfigurer.getSettingsFromFile(file);

				for (Object set : SettingConfigurer.getSettingsMap(loaded).values())
				{
					loadSettings(set);
				}
			}
		} else if (file.getName().contains(".hse"))
		{
			SettingConfigurer settings = (SettingConfigurer) FileProcessor.loadContents(file, FileContent.SETTINGS)
			.get(FileContent.SETTINGS);
			setSettings(settings);
		}
	}

	/*
	 * Load default settings from the default settings file
	 */
	public void loadSettingsFromFile()
	{
		setSettings(SettingConfigurer.loadDefaultSettingsFile());
	}

	/*
	 * Save the settings to a file
	 */
	public void saveSettingsToFile(File file)
	{
		FileSystemInteractor.createOutputFile(file, XMLParser.serializeObject(this));
	}

	/*
	 * Gets the setting component map for the specified settings configuration
	 * 
	 * @param setting - settings to fetch map from
	 * 
	 * @return mapping of all setting components
	 */
	public static HashMap<Class<?>, Object> getSettingsMap(SettingConfigurer setting)
	{
		return setting.settings;
	}

	/*
	 * Loads the default settings from the default settings file
	 */
	public static SettingConfigurer loadDefaultSettingsFile()
	{
		return getSettingsFromFile(
		new File(DataSettings.defaultSettingDirectory + "/" + DataSettings.defaultSettingFileName));
	}

	/*
	 * Gets setting configurer instance from file
	 * 
	 * @param file - file to load instance from
	 * 
	 * @return setting configurer instance from file, or default settings if
	 * load failed
	 */
	public static SettingConfigurer getSettingsFromFile(File file)
	{
		SettingConfigurer settings = null;
		if (file.exists())
		{
			settings = (SettingConfigurer) XMLParser.getObject(file);

		} else
		{
			settings = new SettingConfigurer();
			saveSettingsToFile(file, settings);
		}

		return settings;

	}

	/*
	 * Saves settings to xml file
	 * 
	 * @param file - location to save settings to
	 * 
	 * @param settings - settings configuration to be saved
	 */
	public static void saveSettingsToFile(File file, SettingConfigurer settings)
	{
		try
		{
			String[] fileNameComponents = file.getName().split(Pattern.quote("."));
			if (!fileNameComponents[fileNameComponents.length - 1].equals("xml"))
			{
				String newPath = file.getAbsolutePath() + ".xml";
				file = new File(newPath);
			}
			FileSystemInteractor.createOutputFile(file, XMLParser.serializeObject(settings));
		} catch (Exception badFile)
		{
			badFile.printStackTrace();
		}
	}
}
