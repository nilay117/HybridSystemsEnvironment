package edu.ucsc.cross.hse.core.setting;

/*
 * Version: 2.0 - beshort 10/30/2017
 */
public class ExecutionParameters
{

	/*
	 * Maximum time allowed before execution terminates
	 */
	public int maximumJumps;

	/*
	 * Maximum number of jumps allowed before execution terminates
	 */
	public double maximumTime;

	public void setMaximumTimeAndJumps(Double maximum_time, Integer maximum_jumps)
	{
		maximumTime = maximum_time;
		maximumJumps = maximum_jumps;
	}

	public ExecutionParameters()
	{
		maximumTime = 20.0;
		maximumJumps = 20;
	}

	public ExecutionParameters(Double maximum_time, Integer maximum_jumps)
	{
		maximumTime = maximum_time;
		maximumJumps = maximum_jumps;
	}

}
