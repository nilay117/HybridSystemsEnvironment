package edu.ucsc.cross.hse.core.object.configuration;

public class ExecutionSettings
{

	public boolean runThreadded; // run the environment in a thread (as opposed
									// to blocking code)

	public Integer numberOfTrials; // number of trials to run altogether

	public Integer maxParallelExecutions; // number of threaded environments to
											// run
											// simultaneously (if running more
											// than one)

	public Double simDuration; // duration that the environment will be running
								// if not specified on startup, null indicating
								// unlimited

	public Integer jumpLimit; // maximum number of jumps before terminating the
								// environment, null indicating unlimited

	/*
	 * Default values constructorc
	 */
	public ExecutionSettings()
	{
		numberOfTrials = 1;
		maxParallelExecutions = 1;
		simDuration = 20.0;
		jumpLimit = 100;
		runThreadded = true;
	}
}
