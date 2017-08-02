package edu.ucsc.cross.hse.core.object.configuration;

/*
 * Collection of execution configurations that defines how the environment will
 * be executed
 */
public class ExecutionSettings
{

	/*
	 * Flag to run the environment in a thread (as opposed to blocking code)
	 */
	public Boolean runThreadded;
	/*
	 * number of trials to run altogether. Results will be stored automatically
	 * when running more than one trial
	 */
	public Integer numberOfTrials;
	/*
	 * Number of threaded environments to executed simultaneously (if running
	 * more than one)
	 */
	public Integer maxParallelExecutions;
	/*
	 * duration that the environment will be running if not specified on
	 * startup, null indicating unlimited
	 */
	public Double simDuration;
	/*
	 * maximum number of jumps before terminating the environment, null
	 * indicating unlimited
	 */
	public Integer jumpLimit;
	/*
	 * Flag to terminate and rerun the environment if a fatal error occurs, such
	 * as when an integrator overshoot sends a system out of its domains
	 * incorrectly. Adjustments are made before rerunning
	 */
	public boolean rerunOnFatalErrors;

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
