package edu.ucsc.cross.hse.core.framework.environment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.access.FieldFinder;
import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentAdministrator;
import edu.ucsc.cross.hse.core.framework.component.ComponentOrganizer;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.domain.HybridTime;

public class GlobalContentAdministrator extends ComponentAdministrator
{

	// currently
	// occurring
	@CoreComponent
	public static final HashMap<String, GlobalContentAdministrator> globalSystems = new HashMap<String, GlobalContentAdministrator>();
	private GlobalEnvironmentContent globalSystem;

	private GlobalContentAdministrator(GlobalEnvironmentContent global_system)
	{
		super(global_system);
		globalSystem = global_system;
	}

	public static GlobalContentAdministrator getContentAdministrator(String id)
	{
		GlobalContentAdministrator admin = null;
		// return systems.get(id);
		try
		{
			admin = globalSystems.get(id);
		} catch (Exception noSystem)
		{
			System.err.println("Attempt to access system operator with id " + id + " failed");
			noSystem.printStackTrace();

		}
		return admin;
	}

	public static GlobalContentAdministrator getContentAdministrator(GlobalEnvironmentContent sys)
	{
		GlobalContentAdministrator admin = null;//
		if (globalSystems.containsKey(sys.toString()))
		{
			admin = globalSystems.get(sys.toString());
		} else
		{
			admin = new GlobalContentAdministrator(sys);
			if (!globalSystems.containsKey(sys.toString()))
			{
				globalSystems.put(sys.toString(), admin);
				admin.preinitializeContent();
			}
		}
		return admin;
	}

	public static GlobalEnvironmentContent getGlobalSystem(Component component)
	{
		return globalSystems.get(ComponentAdministrator.getConfigurer(component).getEnvironmentKey()).globalSystem;
	}

	public HybridTime getEnvironmentHybridTime()
	{
		return globalSystem.environmentTime;
	}

	public void setJumpOccurring(boolean jumpOccurring)
	{
		globalSystem.jumpOccurring = jumpOccurring;
	}

	// Preparation Methods
	public void prepareEnvironmentContent()
	{
		prepareComponents();
		initializeTimeDomains();
	}

	private void initializeTimeDomains()
	{
		// systems = new HashMap<String, GlobalHybridSystem>();
		// this.environmentKey = this.toString();
		// systems.put(environmentKey, this);
		if (globalSystem.earthStartTime.seconds() <= 0)
		{
			globalSystem.jumpOccurring = false;
			globalSystem.earthStartTime = Time.newSecondsValue(System.nanoTime() / 1000000000.0);
		}
	}

	void prepareComponents()
	{
		// this.initializeContentMappings(true);
		linkEnvironment();
		initializeComponents(Data.class);
		initializeComponents();
		linkEnvironment();
	}

	protected void preinitializeContent()
	{
		// if (globalSystem.getContents().getComponents(true).size() <= 0)
		{
			globalSystem.jumpOccurring = false;
			globalSystem.environmentTime = new HybridTime();
			globalSystem.earthStartTime = Time.newSecondsValue(-1.0);
			setEnvironmentKey(globalSystem.toString());
		}
		// EnvironmentContentOperator.addGlobalHybridSystem(globalSystem);
		// ComponentAdministrator.getConfigurer(this).setEnvironmentKey(globalSystem.toString());
	}

	private void initializeComponents(Class<?>... components_to_initialize)
	{
		List<Class<?>> initializeList = Arrays.asList(components_to_initialize);
		for (Component component : globalSystem.getContents().getComponents(true))
		{
			boolean initialize = initializeList.size() == 0;
			for (Class<?> checkClass : initializeList)
			{
				initialize = initialize || FieldFinder.containsSuper(component, checkClass);
			}
			if (initialize)
			{
				ComponentAdministrator.getConfigurer(component).protectedInitialize();
			}
		}

	}

	private void linkEnvironment()
	{
		this.initializeContentMappings();
		for (Component component : globalSystem.getContents().getComponents(true))
		{

			ComponentAdministrator.getConfigurer(component)
			.setEnvironmentKey(ComponentAdministrator.getConfigurer(globalSystem).getEnvironmentKey());
		}
	}
}
