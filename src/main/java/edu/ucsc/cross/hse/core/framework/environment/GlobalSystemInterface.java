package edu.ucsc.cross.hse.core.framework.environment;

import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.framework.component.ComponentHierarchy;

public interface GlobalSystemInterface
{

	public boolean isJumpOccurring();

	public ComponentHierarchy components();

	public Time environmentTime();

	public Integer jumpIndex();

}
