package edu.cross.ucsc.hse.core.nnn;

import java.util.Set;

public interface FlowModel<P>
{

	public Set<LinkModel> getFlowLinks();

	public P packetDelivered();

	public Double startTime;
	public Double startData;
	public Double delayTime;
	public Double packetSize;
}
