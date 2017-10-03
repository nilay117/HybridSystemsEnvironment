package edu.ucsc.cross.hse.core.obj.config;

public class ExecutionParameters
{

	public Double simulationDuration;
	public Integer maximumJumps;
	public String saveDirectory;

	public ExecutionParameters()
	{
		simulationDuration = 20.0;
		maximumJumps = 20;
		saveDirectory = null;
	}

	public ExecutionParameters(Double duration, Integer jump_threshold)
	{
		simulationDuration = duration;
		maximumJumps = jump_threshold;
		saveDirectory = null;
	}

	public ExecutionParameters(Double duration, Integer jump_threshold, String save_directory)
	{
		simulationDuration = duration;
		maximumJumps = jump_threshold;
		saveDirectory = save_directory;
	}

}
