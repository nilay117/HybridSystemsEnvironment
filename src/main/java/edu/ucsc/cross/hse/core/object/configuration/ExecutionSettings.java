package edu.ucsc.cross.hse.core.object.configuration;

public class ExecutionSettings
{

	public Boolean runThreadded; // run the environment in a thread (as opposed
									// to blocking code)

	public Integer numberOfTrials; // number of trials to run altogether.
									// Results will be stored automatically when
									// running more than one trial

	public Integer maxParallelExecutions; // number of threaded environments to
											// run
											// simultaneously (if running more
											// than one)

	public Double simDuration; // duration that the environment will be running
								// if not specified on startup, null indicating
								// unlimited

	public Integer jumpLimit; // maximum number of jumps before terminating the
								// environment, null indicating unlimited

	public boolean rerunOnFatalErrors; // terminate and reruns the environment
										// if a fatal error occurs, such as when
										// an integrator overshoot sends a
										// system
										// out of its domains incorrectly.
										// Adjustments are made before rerunning

	/*
	 * Default values constructorc
	 */
	public ExecutionSettings()
	{
		numberOfTrials = 1;
		maxParallelExecutions = 1;
		simDuration = 20.0;
		jumpLimit = 100;
		runThreadded = false;
		rerunOnFatalErrors = true;
	}
}
