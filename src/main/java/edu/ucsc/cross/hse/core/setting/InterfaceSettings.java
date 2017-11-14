package edu.ucsc.cross.hse.core.setting;

public class InterfaceSettings
{

	public boolean runInRealTime; // run environment in real time (if possible)

	public double advanceTimeThresholdNanoseconds; // maximum amount of time ahead of real time before pausing
	// environment in microseconds

	public InterfaceSettings()
	{
		this.runInRealTime = false;
		this.advanceTimeThresholdNanoseconds = 10000000;
	}
}
