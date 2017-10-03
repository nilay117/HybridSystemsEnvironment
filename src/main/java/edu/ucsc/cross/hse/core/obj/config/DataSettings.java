package edu.ucsc.cross.hse.core.obj.config;

import com.jcabi.aspects.Loggable;

public class DataSettings
{

	/*
	 * Time interval between stored data points
	 */
	public Double dataStoreIncrement;

	/*
	 * Location where trial results will be automatically stored (if auto storage is enabled)
	 */
	public String defaultStoreDirectory;

	/*
	 * Flag indicating if settings should be saved in an output file when not specified by the user
	 */
	public Boolean saveToFileByDefault;

	/*
	 * Default value constructor
	 */
	public DataSettings()
	{
		dataStoreIncrement = .2;
		saveToFileByDefault = true;
		defaultStoreDirectory = "results";
	}
}
