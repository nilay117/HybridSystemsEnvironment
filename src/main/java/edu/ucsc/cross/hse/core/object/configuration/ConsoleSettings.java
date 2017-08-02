package edu.ucsc.cross.hse.core.object.configuration;

/*
 * Collection of console configurations that defines how the output will be
 * displayed
 */
public class ConsoleSettings
{

	/*
	 * flag indicating if progress updates should be printed, which display the
	 * current environment time and percentage completed
	 */
	public Boolean printProgressUpdates;

	/*
	 * total number progress updates that will be printed (if progress printing
	 * is enabled)
	 */
	public Integer totalSimTimePrintOuts;

	/*
	 * flag to print notifications when discrete events occur
	 */
	public Boolean printDiscreteEventIndicator;

	/*
	 * flag to include the calling class in any console message printed, ie:
	 * "[EventMonitor] Integrator failure ..."
	 */
	public boolean printCallingClass;

	/*
	 * flag to include the current memory usage in any console message printed,
	 * ie "[2250/11776 Mb][EventMonitor] Integrator failure ..."
	 */
	public boolean printMemoryUsage;

	/*
	 * flag to include the current (real world time) in any console message
	 * printed, ie
	 * "[16:24:12][2250/11776 Mb][EventMonitor] Integrator failure ..."
	 */
	public boolean printCurrentTime;

	/*
	 * Default constructor
	 */
	public ConsoleSettings()
	{
		totalSimTimePrintOuts = 20;
		printProgressUpdates = true;
		printDiscreteEventIndicator = false;
		printCallingClass = true;
		printMemoryUsage = true;
		printCurrentTime = true;

	}

}
