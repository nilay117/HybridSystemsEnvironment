package edu.ucsc.cross.hse.core.object.configuration;

public class DataSettings
{

	public boolean loadSettingsFromFile; // flag to load settings from the
											// default
											// files

	public boolean automaticallyStoreResults; // flag to store results
												// automatically

	public boolean storeDataAtIncrements; // flag to determine whether data is
											// stored at a specified increment,

	public boolean storeAtEveryJump; // flag to always store data when a jump
										// occurs regardless of the specified
										// store interval

	public Double dataStoreIncrement;// .01; // time interval between data
										// points stored

	public String resultAutoStoreDirectory; // location where trial results will
											// be automatically stored (if auto
											// storage is enabled)

	public boolean createResultSubDirectory; // flag to create a subdirectory
												// with the name of the
												// environment to keep results
												// organized when running a
												// variety of trials multiple
												// times

	public Boolean saveDataToFileDefault; // flag indicating if data should be
											// saved in an output file when not
											// specified by the user

	public Boolean saveSettingsToFileDefault; // flag indicating if settings
												// should be saved in an output
												// file when not specified by
												// the user

	public Boolean multiThreadInput; // flag indicating if input data should be
										// read in on multiple threads
	/*
	 * Default value constructor
	 */

	public DataSettings()
	{
		loadSettingsFromFile = true;
		automaticallyStoreResults = false;
		storeDataAtIncrements = true;
		storeAtEveryJump = true;
		dataStoreIncrement = .05;
		resultAutoStoreDirectory = defaultResultsDirectory;
		createResultSubDirectory = true;
		saveDataToFileDefault = true;
		saveSettingsToFileDefault = false;

	}

	public static String defaultSettingFileName = "defaultSettings.xml"; // defalt
	// setting
	// file
	public static String defaultResultsDirectory = "results";

	public static String defaultSettingDirectory = "settings";
}
