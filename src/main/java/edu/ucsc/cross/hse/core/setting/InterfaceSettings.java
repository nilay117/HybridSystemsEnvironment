package edu.ucsc.cross.hse.core.setting;

public class InterfaceSettings
{

	public boolean runInRealTime; // run environment in real time (if possible)

	public double stepSizeNanoseconds; // duration of discretized environment operating step when operating in real time
										// mode. Environment operates intermittently in real time mode to allow delays
										// in operation to synchronize with real time.

	public InterfaceSettings()
	{
		this.runInRealTime = false;
		this.stepSizeNanoseconds = 100000000;
	}
}
