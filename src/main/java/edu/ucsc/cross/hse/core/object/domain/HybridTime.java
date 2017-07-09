package edu.ucsc.cross.hse.core.object.domain;

/*
 * This class describes a hybrid time, which has a discrete and continuous
 * domain. This is the time domain for all hybrid systems
 */
public class HybridTime
{

	private HybridTime current; // the current hybrid time instance
	private Double time; // the current time
	private Integer jumpIndex; // the current jump index

	/*
	 * Constructor with zero time and jump index wuth no current time instance
	 */
	public HybridTime()
	{
		time = 0.0;
		jumpIndex = 0;
		current = null;
	}

	/*
	 * Constructor with zero time and jump index a current time instance
	 */
	public HybridTime(boolean maintain_current)
	{
		time = 0.0;
		jumpIndex = 0;
		current = new HybridTime();
	}

	/*
	 * Constructor with initial time but no initial jump index
	 */
	public HybridTime(Double initial_time)
	{
		time = initial_time;
		jumpIndex = 0;
		current = null;// new HybridTime(initial_time);
	}

	/*
	 * Constructor with initial time and jump index
	 */
	public HybridTime(Double initial_time, Integer initial_jump_index)
	{
		time = initial_time;
		jumpIndex = initial_jump_index;
		current = null;// = new HybridTime(initial_time, initial_jump_index);
	}

	/*
	 * Gets the current time
	 */
	public Double getTime()
	{
		return time;
	}

	/*
	 * Sets the current time
	 */
	public void setTime(Double time)
	{
		this.time = time;
		if (current != null)
		{
			current = new HybridTime(time, jumpIndex);
		}
	}

	/*
	 * Gets the current jump index
	 */
	public Integer getJumpIndex()
	{
		return jumpIndex;
	}

	/*
	 * Increments the jump index by 1
	 */
	public void incrementJumpIndex()
	{
		incrementJumpIndex(1);
	}

	/*
	 * Increments the jump index by more than 1
	 */
	public void incrementJumpIndex(Integer increment)
	{
		Integer newJumpIndex = jumpIndex + increment;
		jumpIndex = newJumpIndex;
		if (current != null)
		{
			current = new HybridTime(time, newJumpIndex);
		}
	}

	/*
	 * Gets the current instance of hybrid time, used for storing data as every
	 * instance will a uniq id
	 */
	public HybridTime getCurrent()
	{
		HybridTime curr = this;
		if (current != null)
		{
			curr = current;
		}
		return curr;
	}

	/*
	 * Sets the current jump index
	 */
	public void setJumpIndex(Integer index)
	{
		jumpIndex = index;
	}
}
