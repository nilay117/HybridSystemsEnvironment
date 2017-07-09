package edu.ucsc.cross.hse.core.object.domain;

/*
 * Holds a small adjustable value that can be used in situations where a
 * condition is something equals zero. Instead of integrating with insanely
 * small step sizes you can use this Threshold instead to save alot of time
 */
public class Threshold
{

	public static Double nonZeroThresholdSize = 0.001; // default value for the
														// threshold
	public static Double zeroRoundThreshold = 0.01; // threshold

	/*
	 * Check if equal zero
	 */
	public static boolean equalsZero(Double value)
	{

		return Math.abs(value) <= zeroRoundThreshold;
	}

	/*
	 * Check if greater than or equal to zero
	 */
	public static boolean gteZero(Double value)
	{
		return value >= -zeroRoundThreshold;
	}

	/*
	 * Check if kess than or equal to zero
	 */
	public static boolean lteZero(Double value)
	{
		return value <= zeroRoundThreshold;
	}

	/*
	 * Toggles if the threshold is enabled or not
	 */
	public static void toggleThreshold(boolean enabled)
	{
		if (enabled)
		{
			adjustThresholdSize(nonZeroThresholdSize);
		} else
		{
			adjustThresholdSize(0.0);
		}
	}

	/*
	 * Set the thresholdSize
	 */
	public static void adjustThresholdSize(Double threshold)
	{
		zeroRoundThreshold = threshold;
		nonZeroThresholdSize = threshold;
	}
}
