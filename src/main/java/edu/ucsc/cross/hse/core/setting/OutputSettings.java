package edu.ucsc.cross.hse.core.setting;

import com.be3short.io.format.ImageFormat;

public class OutputSettings
{

	/*
	 * Time interval between stored data points
	 */
	public double dataPointInterval;

	/*
	 * Location where trial results will be automatically stored (if auto storage is enabled)
	 */
	public String outputDirectory;

	/*
	 * Flag indicating if settings should be saved in an output file when not specified by the user
	 */
	public Boolean saveChartsToFile;

	/*
	 * Flag indicating if settings should be saved in an output file when not specified by the user
	 */
	public ImageFormat chartFileFormat;

	/*
	 * Flag indicating if settings should be saved in an output file when not specified by the user
	 */
	public Boolean saveEnvironmentToFile;

	/*
	 * Flag indicating if settings should be saved in an output file when not specified by the user
	 */
	public Boolean saveConfigurationToFile;
	public boolean appendFilesWithTime;
	/*
	 * Flag indicating if settings should be saved in an output file when not specified by the user
	 */
	public String environmentFileName;

	/*
	 * Flag indicating if settings should be saved in an output file when not specified by the user
	 */
	public String configurationFileName;

	/*
	 * Default value constructor
	 */
	public OutputSettings()
	{
		dataPointInterval = .1;
		saveChartsToFile = true;
		outputDirectory = "output";
		saveEnvironmentToFile = false;
		saveConfigurationToFile = true;
		environmentFileName = "environment";
		configurationFileName = "environmentConfiguration";
		chartFileFormat = ImageFormat.PNG;
		appendFilesWithTime = true;
	}

}
