package edu.ucsc.cross.hse.core.framework.environment;

import bs.commons.objects.access.CoreComponent;
import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOrganizer;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;

/*
 * This is the main root system of the environment that contains all of the
 * components. This system also contains the setting configurations to be easily
 * accessed by the processor and any environment components, and also so that
 * they will be saved when this class is exportated.
 */
public class GlobalEnvironmentContent extends Component
{

	protected HybridTime environmentTime; // time relative to when the
											// environment
											// was launched

	protected Time earthStartTime; // epoch time when the environment was
									// launched

	protected boolean jumpOccurring; // flag indicating if a jump is occurring
										// in
										// any component in the environment

	// Constructors

	public GlobalEnvironmentContent()
	{
		super("Global Environment Hybrid Systems");
		GlobalContentAdministrator.getContentAdministrator(this);
	}

	public GlobalEnvironmentContent(String environment_title)
	{
		super(environment_title);
		GlobalContentAdministrator.getContentAdministrator(this);
	}

	// Domain Accessibility Functions

	public boolean isJumpOccurring()
	{
		return jumpOccurring;
	}

	public Double getEnvironmentTime()
	{
		// TODO Auto-generated method stub
		return environmentTime.getTime();
	}

	public Double getEarthStartTime()
	{
		return earthStartTime.seconds();
	}

	public Integer getJumpIndex()
	{
		// TODO Auto-generated method stub
		return environmentTime.getJumpIndex();
	}

}
