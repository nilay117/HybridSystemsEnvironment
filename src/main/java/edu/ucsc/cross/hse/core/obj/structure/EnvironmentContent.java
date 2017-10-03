package edu.ucsc.cross.hse.core.obj.structure;

import com.jcabi.aspects.Loggable;
import java.util.ArrayList;

@Loggable(Loggable.DEBUG)
public class EnvironmentContent
{

	private HybridTime simTime; // Simulation time
	private ArrayList<HybridSystem<?>> systems;

	public EnvironmentContent()
	{
		simTime = new HybridTime(0.0, 0);
		systems = new ArrayList<HybridSystem<?>>();
	}

	public HybridTime getSimTime()
	{
		return simTime;
	}

	public void setSimTime(HybridTime simTime)
	{
		this.simTime = simTime;
	}

	public void addSystems(HybridSystem<?>... systems)
	{
		for (HybridSystem<?> system : systems)
		{
			this.systems.add(system);
		}
	}

	public ArrayList<HybridSystem<?>> getSystems()
	{
		return systems;
	}

	public void setSystems(ArrayList<HybridSystem<?>> systems)
	{
		this.systems = systems;
	}
}
