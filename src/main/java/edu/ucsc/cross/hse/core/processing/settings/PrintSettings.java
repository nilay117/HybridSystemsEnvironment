package edu.ucsc.cross.hse.core.processing.settings;

public class PrintSettings
{

	public final boolean printProgressUpdates; // = true;
	public Integer totalSimTimePrintOuts; // The total number of times that the
											// current simulation time will be
											// printed to the console, set to
											// null for no notifications
	public final boolean printDiscreteEventIndicator; // flag to print event
														// indicator
	public boolean printStoreDataIndicator; // flag to print store data
											// indicator
	public boolean printStoreDataReport; // flag to print store data indicator

	/*
	 * Default constructor
	 */
	public PrintSettings()
	{
		totalSimTimePrintOuts = 20;
		printStoreDataIndicator = true;
		printProgressUpdates = true;
		printDiscreteEventIndicator = true;
		printStoreDataReport = false;
	}

}
