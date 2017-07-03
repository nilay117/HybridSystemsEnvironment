package edu.ucsc.cross.hse.core.object.domain;

import edu.ucsc.cross.hse.core.processing.execution.HybridEnvironment;

/*
 * Holds a small adjustable value that is for a slightly rounded version of 0.
 * It saved a lot of time when the integrator has to go to a super small steo
 * size.
 */
public class Threshold
{

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
}
