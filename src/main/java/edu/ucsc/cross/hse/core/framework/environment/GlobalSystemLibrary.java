package edu.ucsc.cross.hse.core.framework.environment;

import java.util.HashMap;

import bs.commons.objects.access.CoreComponent;

public class GlobalSystemLibrary
{

	// currently
	// occurring
	@CoreComponent
	public static final HashMap<String, GlobalSystem> systems = new HashMap<String, GlobalSystem>();

	public static GlobalSystem getGlobalSystem(String id)
	{
		return systems.get(id);
	}

	public static void addGlobalHybridSystem(GlobalSystem sys)
	{
		systems.put(sys.toString(), sys);
	}

}
