package edu.ucsc.cross.hse.core.time;

public class HybridTime
{

	private Integer jumps;

	private double time;

	public Integer getJumps()
	{
		return jumps;
	}

	public double getTime()
	{
		return time;
	}

	public HybridTime(double time, Integer jumps)
	{
		this.time = time;
		this.jumps = jumps;
	}

}
