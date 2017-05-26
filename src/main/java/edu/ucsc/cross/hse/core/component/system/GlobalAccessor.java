package edu.ucsc.cross.hse.core.component.system;

import java.util.HashMap;

public class GlobalAccessor
{

	private static HashMap<String, GlobalHybridSystem> systems = new HashMap<String, GlobalHybridSystem>();

	public static GlobalHybridSystem getGlobalSystem(String id)
	{
		return systems.get(id);
	}

	protected static void addGlobalHybridSystem(GlobalHybridSystem sys)
	{
		systems.put(sys.toString(), sys);
	}
}
