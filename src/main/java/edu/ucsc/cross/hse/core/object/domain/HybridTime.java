package edu.ucsc.cross.hse.core.object.domain;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

public class HybridTime
{

	private HybridTime current;
	private Double time;
	private Integer jumpIndex;

	public HybridTime()
	{
		time = 0.0;
		jumpIndex = 0;
		current = null;//new HybridTime();
	}

	public HybridTime(boolean maintain_current)
	{
		time = 0.0;
		jumpIndex = 0;
		current = new HybridTime();
	}

	public HybridTime(Double initial_time)
	{
		time = initial_time;
		jumpIndex = 0;
		current = null;//new HybridTime(initial_time);
	}

	public HybridTime(Double initial_time, Integer initial_jump_index)
	{
		time = initial_time;
		jumpIndex = initial_jump_index;
		current = null;//= new HybridTime(initial_time, initial_jump_index);
	}

	public Double getTime()
	{
		return time;
	}

	public void setTime(Double time)
	{
		this.time = time;
		if (current != null)
		{
			current = new HybridTime(time, jumpIndex);
		}
	}

	public Integer getJumpIndex()
	{
		return jumpIndex;
	}

	public void incrementJumpIndex()
	{
		incrementJumpIndex(1);
	}

	public void incrementJumpIndex(Integer increment)
	{
		jumpIndex = jumpIndex + increment;
		if (current != null)
		{
			current = new HybridTime(time, jumpIndex);
		}
	}

	public HybridTime getCurrent()
	{
		HybridTime curr = this;
		if (current != null)
		{
			curr = current;
		}
		return curr;
	}

	//	@Override
	//	public void writeExternal(ObjectOutput out) throws IOException
	//	{
	//		out.writeObject(jumpIndex);
	//		out.writeObject(time);
	//
	//	}
	//
	//	@Override
	//	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	//	{
	//
	//		jumpIndex = (Integer) in.readObject();
	//		time = (Double) in.readObject();
	//
	//	}
}
