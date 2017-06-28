package edu.ucsc.cross.hse.core.object.domain;

import edu.ucsc.cross.hse.core.processing.execution.HybridEnvironment;

public class Threshold
{

	public static Double zeroRoundThreshold = 0.01;

	public static boolean equalsZero(Double value)
	{

		return Math.abs(value) <= zeroRoundThreshold;
	}

	public static boolean gteZero(Double value)
	{
		return value >= -zeroRoundThreshold;
	}

	public static boolean lteZero(Double value)
	{
		return value <= zeroRoundThreshold;
	}
}
