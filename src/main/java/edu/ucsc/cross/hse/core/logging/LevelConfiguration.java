package edu.ucsc.cross.hse.core.logging;

public class LevelConfiguration
{

	public boolean printDebug;
	public boolean printInfo;
	public boolean printWarning;
	public boolean printError;

	public LevelConfiguration()
	{
		printDebug = false;
		printInfo = true;
		printWarning = true;
		printError = true;
	}
}
