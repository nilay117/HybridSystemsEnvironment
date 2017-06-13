package edu.ucsc.cross.hse.core.framework.environment;

import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.framework.component.ComponentHierarchy;

public interface GlobalSystemInterface
{

	public boolean isJumpOccurring();

	public ComponentHierarchy getContents();

	public Double getEnvironmentTime();

	public Double getEarthStartTime();

	public Integer getJumpIndex();

}
