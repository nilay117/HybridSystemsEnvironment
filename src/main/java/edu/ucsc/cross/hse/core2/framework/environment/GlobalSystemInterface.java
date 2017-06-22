package edu.ucsc.cross.hse.core2.framework.environment;

import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.framework.component.ComponentOrganizer;

public interface GlobalSystemInterface
{

	public boolean isJumpOccurring();

	public ComponentOrganizer getContents();

	public Double getEnvironmentTime();

	public Double getEarthStartTime();

	public Integer getJumpIndex();

}
