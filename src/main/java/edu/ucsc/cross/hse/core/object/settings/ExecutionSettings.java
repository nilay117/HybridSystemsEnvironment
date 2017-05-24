package edu.ucsc.cross.hse.core.object.settings;

public class ExecutionSettings
{

	public String name;

	public boolean runThreadded;

	public Integer numberOfTrials;

	public Integer maxParallelExecutions;

	public Double simDuration;

	public Integer jumpLimit;

	public ExecutionSettings()
	{
		numberOfTrials = 1;
		maxParallelExecutions = 1;
		simDuration = 10.0;
		jumpLimit = 20;
		runThreadded = true;
	}
}
