package edu.ucsc.cross.hse.core.object.settings;

public class ExecutionSettings
{

	public boolean runThreadded; // run the environment in a thread

	public Integer numberOfTrials; // number of trials to run altogether

	public Integer maxParallelExecutions; // number of threaded trials to run
											// simultaneously

	public Double simDuration; // duration of the trial

	public Integer jumpLimit; // maximum number of jumps allowed within the
								// trial

	/*
	 * Default values constructorc
	 */
	public ExecutionSettings()
	{
		numberOfTrials = 1;
		maxParallelExecutions = 1;
		simDuration = 15.0;
		jumpLimit = 20;
		runThreadded = true;
	}
}
