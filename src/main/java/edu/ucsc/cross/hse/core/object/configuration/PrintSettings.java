package edu.ucsc.cross.hse.core.object.configuration;

public class PrintSettings
{

	public Boolean printProgressUpdates; // flag indicating if progress
											// updates should be printed,
											// which display the current
											// environment time and
											// percentage completed

	public Integer totalSimTimePrintOuts; // total number progress updates that
											// will be printed (if progress
											// printing is enabled)

	public Boolean printDiscreteEventIndicator; // flag to print notifications
												// when discrete events occur

	public boolean printCallingClass; // flag to include the calling class in
										// any console message printed, ie:
										// "[EventMonitor] Integrator failure
										// bla bla bla"

	public boolean printMemoryUsage; // flag to include the current memory usage
										// in any console message printed, ie
										// "[2250/11776 Mb][EventMonitor]
										// Integrator failure bla bla bla..."

	public boolean printCurrentTime; // flag to include the current (real world
										// time) in any console message printed,
										// ie "[16:24:12][2250/11776
										// Mb][EventMonitor] Integrator failure
										// bla bla bla..."
	/*
	 * Default constructor
	 */

	public PrintSettings()
	{
		totalSimTimePrintOuts = 20;
		printProgressUpdates = true;
		printDiscreteEventIndicator = false;
		printCallingClass = true;
		printMemoryUsage = true;
		printCurrentTime = true;

	}

}
