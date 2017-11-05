package edu.ucsc.cross.hse.core.setting;

import com.be3short.io.format.ImageFormat;

public class OutputSettings
{

	/*
	 * Flag indicating if file names should include the date at the end (for uniqueness)
	 */
	public boolean appendFilesWithNumericDate;

	/*
	 * Default image file format to be used when exporting charts with no format specified
	 */
	public ImageFormat chartFileFormat;

	/*
	 * Name of the environment configuration file if it were to be saved
	 */
	public String configurationFileName;

	/*
	 * Time between data point storage
	 */
	public double dataPointInterval;

	/*
	 * Name of the environment file if it were to be saved
	 */
	public String environmentFileName;

	/*
	 * Location where results will be automatically stored if auto storage is enabled
	 */
	public String outputDirectory;

	/*
	 * Flag indicating if charts should be saved in an output file when not specified by the user
	 */
	public Boolean saveChartsToFile;

	/*
	 * Flag indicating if environment configuration should be saved in an output file when not specified by the user
	 */
	public Boolean saveConfigurationToFile;

	/*
	 * Flag indicating if environment should be saved in an output file when not specified by the user
	 */
	public Boolean saveEnvironmentToFile;

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
		appendFilesWithNumericDate = true;
	}

}
