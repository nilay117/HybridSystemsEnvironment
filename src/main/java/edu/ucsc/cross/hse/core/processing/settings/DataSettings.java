package edu.ucsc.cross.hse.core.processing.settings;

public class DataSettings
{

	public static String defaultSettingFileName = "default.xml"; // defalt
																	// setting
																	// file
	public static String defaultSettingDirectory = "./settings";
	public static Double postJumpStoreIncrement = .0000000001; // amount of time
																// subtracted to
																// actual time
																// before a jump
																// occurs, for
																// graphing
																// purposes

	public boolean automaticallyStoreResults; // flag to store results
												// automatically

	public boolean storeDataAtIncrements; // flag to determine whether data is
											// stored at a specified increment,

	public boolean storeAtEveryJump; // flag to always store data when a jump
										// occurs regardless of the specified
										// store interval

	public Double dataStoreIncrement;// .01; // time interval between data
										// points stored

	public String autoStoreDirectory; // location where new directories will be
										// created containing results

	public boolean environmentNameSubDirectory; // flag to create a subdirectory
												// with the name of the
												// environment

	public boolean storePreJumpValue; // flag to store data values before a jump
										// ocurs

	/*
	 * Default value constructor
	 */
	public DataSettings()
	{

		storePreJumpValue = true;
		automaticallyStoreResults = false; // flag to store results
											// automatically
		storeAtEveryJump = false;
		storeDataAtIncrements = true; // flag to determine whether data is
										// stored at a specified increment,
		dataStoreIncrement = .1;// .01; // time interval between data
								// points
								// stored
		autoStoreDirectory = "./results"; // location where new directories will
											// be created containing results
		environmentNameSubDirectory = true;
	}

}