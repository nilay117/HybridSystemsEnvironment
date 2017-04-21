package edu.ucsc.cross.hybrid.env.core.settings;

public class TrialSettings
{

	public String name;

	public boolean runThreadded;

	public Integer numberOfTrials;

	public Integer maxParallelExecutions;

	public Double simDuration;

	public TrialSettings()
	{
		numberOfTrials = 1;
		maxParallelExecutions = 1;
		simDuration = 10.0;
		runThreadded = true;
	}
}
