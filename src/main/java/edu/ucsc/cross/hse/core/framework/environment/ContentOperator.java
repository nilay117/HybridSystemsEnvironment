package edu.ucsc.cross.hse.core.framework.environment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.be3short.data.cloning.ObjectCloner;

import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.access.FieldFinder;
import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;

public class ContentOperator extends ComponentOperator
{

	// currently
	// occurring
	@CoreComponent
	public static final HashMap<String, ContentOperator> globalSystems = new HashMap<String, ContentOperator>();
	private EnvironmentContent globalSystem;

	private ContentOperator(EnvironmentContent global_system)
	{
		super(global_system);
		globalSystem = global_system;

	}

	public static ContentOperator getOperator(String id)
	{
		ContentOperator admin = null;
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

	public static ContentOperator getOperator(EnvironmentContent sys)
	{
		ContentOperator admin = null;//
		if (globalSystems.containsKey(sys.toString()))
		{
			admin = globalSystems.get(sys.toString());
		} else
		{
			admin = new ContentOperator(sys);
			if (!globalSystems.containsKey(sys.toString()))
			{
				globalSystems.put(sys.toString(), admin);
				admin.preinitializeContent();
			}
		}
		return admin;
	}

	public static EnvironmentContent getGlobalSystem(Component component)
	{
		return globalSystems.get(ComponentOperator.getOperator(component).getEnvironmentKey()).globalSystem;
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

	public void initializeTimeDomains()
	{
		globalSystem.jumpOccurring = false;
		globalSystem.environmentTime = new HybridTime(true);
		globalSystem.jumpOccurring = false;
		globalSystem.earthStartTime = System.nanoTime() / 1000000000.0;

	}

	void prepareComponents()
	{
		// this.initializeContentMappings(true);
		linkEnvironment();
		initializeComponents(Data.class);
		initializeComponents();
		generateAddresses();
		linkEnvironment();
	}

	protected void preinitializeContent()
	{

		globalSystem.jumpOccurring = false;
		globalSystem.environmentTime = new HybridTime(true);
		globalSystem.earthStartTime = -1.0;
		setEnvironmentKey(globalSystem.toString());
		// globalSystem.dataLinks = new HashMap<String, Data>();

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
				ComponentOperator.getOperator(component).protectedInitialize();
			}
		}

		storeConfiguration();
	}

	private void generateAddresses()
	{
		for (Component component : globalSystem.getContents().getComponents(true))
		{

			ComponentOperator.getOperator(component).generateAddress();
		}
	}

	private void linkEnvironment()
	{
		this.initializeContentMappings();
		for (Component component : globalSystem.getContents().getComponents(true))
		{

			ComponentOperator.getOperator(component)
			.setEnvironmentKey(ComponentOperator.getOperator(globalSystem).getEnvironmentKey());
		}
	}

}
