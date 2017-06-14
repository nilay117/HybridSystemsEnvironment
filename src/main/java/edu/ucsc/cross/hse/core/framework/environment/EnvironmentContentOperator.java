package edu.ucsc.cross.hse.core.framework.environment;

import java.util.HashMap;

import bs.commons.objects.access.CoreComponent;
import edu.ucsc.cross.hse.core.framework.domain.HybridTime;

public class EnvironmentContentOperator
{

	// currently
	// occurring
	@CoreComponent
	public static final HashMap<String, EnvironmentContentOperator> globalSystems = new HashMap<String, EnvironmentContentOperator>();
	private EnvironmentContent globalSystem;

	public EnvironmentContentOperator(EnvironmentContent global_system)
	{
		globalSystem = global_system;
	}

	public static EnvironmentContent getGlobalSystem(String id)
	{
		EnvironmentContent returnSystem = null;
		// return systems.get(id);
		try
		{
			returnSystem = globalSystems.get(id).getGlobalSystem();
		} catch (Exception noSystem)
		{
			// System.err.println("Attempt to access system with id " + id + "
			// failed");
			// noSystem.printStackTrace();

		}
		return returnSystem;
	}

	public static EnvironmentContentOperator getGlobalSystemOperator(String id)
	{
		EnvironmentContentOperator returnSystem = null;
		// return systems.get(id);
		try
		{
			returnSystem = globalSystems.get(id);
		} catch (Exception noSystem)
		{
			System.err.println("Attempt to access system operator with id " + id + " failed");
			noSystem.printStackTrace();

		}
		return returnSystem;
	}

	public static void addGlobalHybridSystem(EnvironmentContent sys)
	{
		globalSystems.put(sys.toString(), new EnvironmentContentOperator(sys));
	}

	public EnvironmentContent getGlobalSystem()
	{
		return globalSystem;
	}

	public HybridTime getEnvironmentHybridTime()
	{
		return globalSystem.getEnvironmentHybridTime();
	}

	public void setJumpOccurring(boolean jumpOccurring)
	{
		globalSystem.setJumpOccurring(jumpOccurring);
	}

}
