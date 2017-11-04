package edu.ucsc.cross.hse.core.container;

import com.be3short.obj.modification.ObjectCloner;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import java.util.ArrayList;

public class EnvironmentContent
{

	private ArrayList<HybridSystem<?>> systems;

	public void add(HybridSystem<?>... new_systems)
	{
		for (HybridSystem<?> sys : new_systems)
		{
			if (!systems.contains(sys))
			{
				systems.add(sys);
			}
		}
	}

	public void add(HybridSystem<?> system, Integer quantity)
	{
		add(system);
		for (Integer i = 1; i < quantity; i++)
		{
			HybridSystem<?> copy = system.getClass().cast(ObjectCloner.xmlClone(system));
			add(copy);
		}
	}

	public ArrayList<HybridSystem<?>> getSystems()
	{
		return systems;
	}

	public void load(ArrayList<HybridSystem<?>> systems)
	{
		this.systems = systems;
	}

	public void load(EnvironmentContent content)
	{
		this.systems = content.systems;
	}

	public void remove(HybridSystem<?>... remove_systems)
	{

		for (HybridSystem<?> sys : remove_systems)
		{
			if (systems.contains(sys))
			{
				systems.remove(sys);
			}
		}

	}

	public EnvironmentContent()
	{
		systems = new ArrayList<HybridSystem<?>>();
	}

}
