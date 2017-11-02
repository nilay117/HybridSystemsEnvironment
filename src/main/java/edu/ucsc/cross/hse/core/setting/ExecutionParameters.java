package edu.ucsc.cross.hse.core.setting;

/*
 * Version: 2.0 - beshort 10/30/2017
 */
public class ExecutionParameters
{

	private double maximumTime;
	private int maximumJumps;

	public ExecutionParameters()
	{
		maximumTime = 20.0;
		maximumJumps = 20;
	}

	public ExecutionParameters(Double duration, Integer jump_threshold)
	{
		maximumTime = duration;
		maximumJumps = jump_threshold;
	}

	public ExecutionParameters(Double duration, Integer jump_threshold, String save_directory)
	{
		maximumTime = duration;
		maximumJumps = jump_threshold;
	}

	public double getMaximumTime()
	{
		return maximumTime;
	}

	public int getMaximumJumps()
	{
		return maximumJumps;
	}

	public void setMaximumTime(double max_time)
	{
		maximumTime = max_time;
	}

	public void setMaximumJumps(int jump_threshold)
	{

		maximumJumps = jump_threshold;

	}

	public void setMaximumTimeAndJumps(Double time_threshold, Integer jump_threshold)
	{
		maximumTime = time_threshold;
		maximumJumps = jump_threshold;
	}
}
