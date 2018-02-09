package edu.ucsc.cross.hse.core.container;

import com.be3short.obj.modification.ObjectCloner;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import edu.ucsc.cross.hse.core.object.Network;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.util.ArrayList;

public class EnvironmentContents
{

	private ArrayList<Network<?, ?, ?>> networks;
	private ArrayList<HybridSystem<?>> systems;
	private HybridTime time;

	/*
	 * Add new hybrid systems to the environment contents
	 * 
	 * @param new_systems - list of systems to add
	 */
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

	/*
	 * Add multiple copies of a hybrid system to the environment contents
	 * 
	 * @param new_systems - list of systems to add
	 */
	public void add(HybridSystem<?> system, Integer quantity)
	{
		add(system);
		for (Integer i = 1; i < quantity; i++)
		{
			HybridSystem<?> copy = system.getClass().cast(ObjectCloner.xmlClone(system));
			add(copy);
		}
	}

	/*
	 * Add new network topologies to the environment contents
	 * 
	 * @param new_networks - list of networks to add
	 */
	public void add(Network<?, ?, ?>... new_networks)
	{
		for (Network<?, ?, ?> sys : new_networks)
		{
			if (!networks.contains(sys))
			{
				networks.add(sys);
			}
		}
	}

	public ArrayList<Network<?, ?, ?>> getNetworks()
	{
		return networks;
	}

	public ArrayList<HybridSystem<?>> getSystems()
	{
		return systems;
	}

	public void load(EnvironmentContents content)
	{
		this.networks = content.networks;
	}

	public void loadNetworks(ArrayList<Network<?, ?, ?>> networks)
	{
		this.networks = networks;
	}

	public void loadSystems(ArrayList<HybridSystem<?>> systems)
	{
		this.systems = systems;
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

	public void remove(Network<?, ?, ?>... remove_networks)
	{

		for (Network<?, ?, ?> sys : remove_networks)
		{
			if (networks.contains(sys))
			{
				networks.remove(sys);
			}
		}

	}

	public EnvironmentContents()
	{
		systems = new ArrayList<HybridSystem<?>>();
		networks = new ArrayList<Network<?, ?, ?>>();
		time = new HybridTime(0.0, 0);
		// addresses.put(this, new HashMap<Integer, HybridSystem<?>>());
	}

	public HybridTime getTime()
	{
		return time;
	}

	public void setTime(HybridTime time)
	{
		this.time = time;
	}

}
