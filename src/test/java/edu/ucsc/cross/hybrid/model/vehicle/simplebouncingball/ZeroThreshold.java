package edu.ucsc.cross.hybrid.model.vehicle.simplebouncingball;

public class ZeroThreshold
{

	private static Double threshold = .01;

	public static void setThreshold(Double thresholds)
	{
		threshold = thresholds;
	}

	public static Double getAdjustedValue(Double value)
	{
		Double abs = Math.abs(value);
		if (abs < threshold)
		{
			return 0.0;
		} else
		{
			return value;
		}
	}
}
