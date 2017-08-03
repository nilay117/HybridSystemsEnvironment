package edu.ucsc.cross.hse.core.framework.environment;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;

/*
 * This is the main root system of the environment that contains all of the
 * components. This system also contains the setting configurations to be easily
 * accessed by the processor and any environment components, and also so that
 * they will be saved when this class is exportated.
 */
public class HybridEnvironment extends Component
{

	/*
	 * Time relative to when the environment was launched
	 */
	protected HybridTime environmentTime;

	/*
	 * Epoch time when the environment was launched
	 */
	protected Double earthStartTime;

	/*
	 * Flag indicating if a jump is occurring in any component within the
	 * environment
	 */
	protected boolean jumpOccurring;

	/*
	 * Flag indicating that a jump is currently occuring in any component in the
	 * environment
	 */
	public boolean isJumpOccurring()
	{
		return jumpOccurring;
	}

	/*
	 * Constructor that gives the environment a general title
	 */
	public HybridEnvironment()
	{
		super("Global Environment Hybrid Systems");
		EnvironmentOperator.getOperator(this);
	}

	/*
	 * Constructor that gives the environment a specified title
	 */
	public HybridEnvironment(String environment_title)
	{
		super(environment_title);
		EnvironmentOperator.getOperator(this);
	}

	/*
	 * Get the current environment time
	 */
	public Double getEnvironmentTime()
	{
		// TODO Auto-generated method stub
		return environmentTime.getTime();
	}

	/*
	 * Get the earth time that the environment was launched
	 */
	public Double getEarthStartTime()
	{
		return earthStartTime;
	}

	/*
	 * Get the current jump index
	 */
	public Integer getJumpIndex()
	{
		// TODO Auto-generated method stub
		return environmentTime.getJumpIndex();
	}

}
