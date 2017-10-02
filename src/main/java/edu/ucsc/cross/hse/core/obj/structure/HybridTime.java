package edu.ucsc.cross.hse.core.obj.structure;

public class HybridTime
{

	public double getTime()
	{
		return time;
	}

	public Integer getJumps()
	{
		return jumps;
	}

	private double time;
	private Integer jumps;

	public HybridTime(double time, Integer jumps)
	{
		this.time = time;
		this.jumps = jumps;
	}

}
