package edu.ucsc.cross.hse.core.exe.interfacing;

import edu.ucsc.cross.hse.core.exe.operator.EnvironmentManager;
import edu.ucsc.cross.hse.core.obj.structure.HybridTime;

public class TimeInterface
{

	private EnvironmentManager manager;

	public TimeInterface(EnvironmentManager manager)
	{
		this.manager = manager;

	}

	public void updateSimulationTime(HybridTime simTime)
	{
		manager.getContents().setSimTime(simTime);
	}

	public void updateSimulationTime(Double simTime)
	{
		manager.getContents().setSimTime(new HybridTime(simTime, manager.getContents().getSimTime().getJumps()));
	}

	public void updateSimulationTime(Double simTime, Integer jump_increment)

	{
		manager.getContents()
		.setSimTime(new HybridTime(simTime, manager.getContents().getSimTime().getJumps() + jump_increment));
	}

	public void incrementJumpIndex(Integer jump_increment)
	{
		manager.getContents().setSimTime(new HybridTime(manager.getContents().getSimTime().getTime(),
		manager.getContents().getSimTime().getJumps() + jump_increment));
	}

	public HybridTime getHybridSimulationTime()
	{
		return manager.getContents().getSimTime();
	}

	public Double getSimulationTime()
	{
		return manager.getContents().getSimTime().getTime();
	}
}
