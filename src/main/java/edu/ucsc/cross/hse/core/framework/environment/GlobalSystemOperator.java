package edu.ucsc.cross.hse.core.framework.environment;

import java.util.HashMap;

import bs.commons.objects.access.CoreComponent;

public class GlobalSystemOperator
{

	// currently
	// occurring
	@CoreComponent
	public static final HashMap<String, GlobalSystemOperator> globalSystems = new HashMap<String, GlobalSystemOperator>();
	private GlobalSystem globalSystem;

	public GlobalSystemOperator(GlobalSystem global_system)
	{
		globalSystem = global_system;
	}

	public static GlobalSystem getGlobalSystem(String id)
	{
		GlobalSystem returnSystem = null;
		//return systems.get(id);
		try
		{
			returnSystem = globalSystems.get(id).getGlobalSystem();
		} catch (Exception noSystem)
		{
			System.err.println("Attempt to access system with id " + id + " failed");
			noSystem.printStackTrace();

		}
		return returnSystem;
	}

	public static GlobalSystemOperator getGlobalSystemOperator(String id)
	{
		GlobalSystemOperator returnSystem = null;
		//return systems.get(id);
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

	public static void addGlobalHybridSystem(GlobalSystem sys)
	{
		globalSystems.put(sys.toString(), new GlobalSystemOperator(sys));
	}

	public GlobalSystem getGlobalSystem()
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
