package edu.ucsc.cross.hse.core.framework.environment;

import java.util.HashMap;

public class GlobalAccessor
{

	private static HashMap<String, GlobalSystem> systems = new HashMap<String, GlobalSystem>();

	public static GlobalSystem getGlobalSystem(String id)
	{
		return systems.get(id);
	}

	protected static void addGlobalHybridSystem(GlobalSystem sys)
	{
		systems.put(sys.toString(), sys);
	}
}
