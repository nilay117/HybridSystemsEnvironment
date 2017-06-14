package edu.ucsc.cross.hse.core.framework.environment;

import bs.commons.objects.access.CoreComponent;
import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentHierarchy;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;

/*
 * This is the main root system of the environment that contains all of the
 * components. This system also contains the setting configurations to be easily
 * accessed by the processor and any environment components, and also so that
 * they will be saved when this class is exportated.
 */
public class GlobalSystem extends Component
{

	@CoreComponent
	private HybridTime environmentTime; // time relative to when the environment
	@CoreComponent // was launched and jump index
	private Time earthStartTime;
	@CoreComponent
	private boolean jumpOccurring; // flag indicating if a jump is

	// Constructors

	public GlobalSystem()
	{
		super("Global Environment Hybrid Systems");
		initializeComponents();
	}

	public GlobalSystem(String environment_title)
	{
		super(environment_title);
		initializeComponents();
	}

	// User accessibility functions

	public boolean isJumpOccurring()
	{
		return jumpOccurring;
	}

	public Double getEnvironmentTime()
	{
		// TODO Auto-generated method stub
		return this.getEnvironmentHybridTime().getTime();
	}

	public Double getEarthStartTime()
	{
		return earthStartTime.seconds();
	}

	public Integer getJumpIndex()
	{
		// TODO Auto-generated method stub
		return this.getEnvironmentHybridTime().getJumpIndex();
	}

	// Private Operational Functions

	private void initializeComponents()
	{
		jumpOccurring = false;
		environmentTime = new HybridTime();
		earthStartTime = Time.newSecondsValue(-1.0);
		ComponentOperator.getConfigurer(this).setEnvironmentKey(this.toString());
		GlobalSystemOperator.addGlobalHybridSystem(this);
		ComponentOperator.getConfigurer(this).setEnvironmentKey(this.toString());
	}

	@Override
	public void initialize()
	{
		// systems = new HashMap<String, GlobalHybridSystem>();
		// this.environmentKey = this.toString();
		// systems.put(environmentKey, this);
		jumpOccurring = false;
		earthStartTime = Time.newSecondsValue(System.nanoTime() / 1000000000.0);
	}

	HybridTime getEnvironmentHybridTime()
	{
		return environmentTime;
	}

	void setJumpOccurring(boolean jumpOccurring)
	{
		this.jumpOccurring = jumpOccurring;
	}
}
