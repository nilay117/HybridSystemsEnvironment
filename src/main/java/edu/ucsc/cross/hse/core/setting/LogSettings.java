package edu.ucsc.cross.hse.core.setting;

public class LogSettings
{

	/*
	 * Total number of progress updates to print, ie 10 prints an update at 10% intervals. -1 disables progress update
	 * print outs
	 */
	public Integer numProgressUpdateOutputs;

	/*
	 * Print debug messages
	 */
	public boolean printDebug;

	/*
	 * Print error messages
	 */
	public boolean printError;

	/*
	 * Print information messages
	 */
	public boolean printInfo;

	/*
	 * Print integrator exception warnings
	 */
	public boolean printIntegratorExceptions;

	/*
	 * Print warning messages
	 */
	public boolean printWarning;

	/*
	 * Inverval between status print outs
	 */
	public Double statusPrintInterval;

	public LogSettings()
	{
		this.statusPrintInterval = 5.0;
		this.numProgressUpdateOutputs = 10;
		printIntegratorExceptions = false;
		printInfo = true;
		printDebug = false;
	}

	public static String debugLabel = "debug";
	public static String errorLabel = "error";
	public static String infoLabel = "info";
	public static String warnLabel = "warn";
}
