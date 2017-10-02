package edu.ucsc.cross.hse.core.obj.config;

public class DataSettings
{

	/*
	 * Flag to load settings from the default files
	 */
	public boolean loadSettingsFromFile;

	/*
	 * Flag to store results automatically
	 */
	public boolean automaticallyStoreResults;

	/*
	 * Time interval between stored data points
	 */
	public Double dataStoreIncrement;

	/*
	 * Location where trial results will be automatically stored (if auto storage is enabled)
	 */
	public String resultAutoStoreDirectory;

	/*
	 * Flag to create a subdirectory with the name of the environment to keep results organized when running a variety
	 * of trials multiple times
	 */
	public boolean createResultSubDirectory;

	/*
	 * Flag indicating if data should be saved in an output file when not specified by the user
	 */
	public Boolean saveDataToFileDefault;

	/*
	 * Flag indicating if settings should be saved in an output file when not specified by the user
	 */
	public Boolean saveSettingsToFileDefault;

	/*
	 * Flag indicating if input data should be read in on multiple threads
	 */
	public Boolean multiThreadInput;

	/*
	 * Default value constructor
	 */
	public DataSettings()
	{
		loadSettingsFromFile = true;
		automaticallyStoreResults = false;
		dataStoreIncrement = .2;
		resultAutoStoreDirectory = defaultResultsDirectory;
		createResultSubDirectory = true;
		saveDataToFileDefault = true;
		saveSettingsToFileDefault = false;

	}

	/*
	 * Default settings file name
	 */
	public static String defaultSettingFileName = "defaultSettings.xml";

	/*
	 * Default results directory
	 */
	public static String defaultResultsDirectory = "results";

	/*
	 * Default settings directory
	 */
	public static String defaultSettingDirectory = "settings";

}
