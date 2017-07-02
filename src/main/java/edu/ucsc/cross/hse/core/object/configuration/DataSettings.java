package edu.ucsc.cross.hse.core.object.configuration;

import com.be3short.data.compression.CompressionFormat;

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

	}

	public static String defaultSettingFileName = "defaultSettings.xml"; // defalt
	// setting
	// file
	public static String defaultResultsDirectory = "results";

	public static String defaultSettingDirectory = "settings";
}
