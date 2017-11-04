package edu.ucsc.cross.hse.core.setting;

/*
 * Settings that configure how environment functions are performed. These settings should not need to be modified, but
 * are available in case the environment is not functioning properly.
 */
public class FunctionalitySettings
{

	/*
	 * Amount of time between file save attempts when creating a chart file output. This duration is needed because the
	 * charts take an unknown amount of time to render, thus the rendering might not be complete when the save is
	 * attempted.
	 */
	public Integer outputImageFileAttemptInterval; // in milliseconds

	/*
	 * Flag to run operations in parallel by multithreading
	 */
	public boolean runThreadedOperations;

	public FunctionalitySettings()
	{
		outputImageFileAttemptInterval = 1000;
		runThreadedOperations = true;
	}
}
