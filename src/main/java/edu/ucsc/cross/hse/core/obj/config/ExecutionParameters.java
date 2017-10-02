package edu.ucsc.cross.hse.core.obj.config;

public class ExecutionParameters
{

	public Double simulationDuration;
	public Integer maximumJumps;

	public ExecutionParameters()
	{
		simulationDuration = 10.0;
		maximumJumps = 2000;
	}

	public ExecutionParameters(Double duration, Integer jump_threshold)
	{
		simulationDuration = duration;
		maximumJumps = jump_threshold;
	}
}
