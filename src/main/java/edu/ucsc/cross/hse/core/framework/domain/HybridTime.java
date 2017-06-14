package edu.ucsc.cross.hse.core.framework.domain;

public class HybridTime
{

	private Double time;
	private Integer jumpIndex;

	public HybridTime()
	{
		time = 0.0;
		jumpIndex = 0;
	}

	public HybridTime(Double initial_time)
	{
		time = initial_time;
		jumpIndex = 0;
	}

	public Double getTime()
	{
		return time;
	}

	public void setTime(Double time)
	{
		this.time = time;
	}

	public Integer getJumpIndex()
	{
		return jumpIndex;
	}

	public void incrementJumpIndex()
	{
		incrementJumpIndex(0);
	}

	public void incrementJumpIndex(Integer increment)
	{
		jumpIndex = jumpIndex + increment;
	}

}
