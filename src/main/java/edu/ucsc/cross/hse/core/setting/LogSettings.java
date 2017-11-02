package edu.ucsc.cross.hse.core.setting;

public class LogSettings
{

	public Double statusPrintInterval;
	public Integer numStatusUpdateOutputs;
	public boolean printDebug;
	public boolean printInfo;
	public boolean printWarning;
	public boolean printError;
	public boolean printIntegratorExceptions;

	public LogSettings()
	{
		this.statusPrintInterval = 5.0;
		this.numStatusUpdateOutputs = 10;
		printIntegratorExceptions = false;
		printInfo = true;
		printDebug = false;
	}

	public static String infoLabel = "info";
	public static String debugLabel = "debug";
	public static String warnLabel = "warn";
	public static String errorLabel = "error";
}
