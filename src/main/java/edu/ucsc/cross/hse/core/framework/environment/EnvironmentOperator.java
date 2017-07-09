package edu.ucsc.cross.hse.core.framework.environment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.be3short.data.cloning.ObjectCloner;

import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.access.FieldFinder;
import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.FullComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;

/*
 * This
 */
public class EnvironmentOperator extends FullComponentOperator
{

	@CoreComponent // mapping of all open global systems
	public static final HashMap<String, EnvironmentOperator> globalSystems = new HashMap<String, EnvironmentOperator>();

	private HybridEnvironment globalSystem; // environment this operator is
											// responsible for

	/*
	 * Constructor that links an environment to its operator
	 */
	private EnvironmentOperator(HybridEnvironment global_system)
	{
		super(global_system);
		globalSystem = global_system;

	}

	/*
	 * Access a components operator using its ID
	 */
	public static EnvironmentOperator getOperator(String id)
	{
		EnvironmentOperator admin = null;
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

	/*
	 * Access a components operator using the global environment
	 */
	public static EnvironmentOperator getOperator(HybridEnvironment sys)
	{
		EnvironmentOperator admin = null;//
		if (globalSystems.containsKey(sys.toString()))
		{
			admin = globalSystems.get(sys.toString());
		} else
		{
			admin = new EnvironmentOperator(sys);
			if (!globalSystems.containsKey(sys.toString()))
			{
				globalSystems.put(sys.toString(), admin);
				admin.preinitializeContent();
			}
		}
		return admin;
	}

	/*
	 * Get the HybridEnvironment that contains the specified component
	 */
	public static HybridEnvironment getGlobalSystem(Component component)
	{
		return globalSystems.get(FullComponentOperator.getOperator(component).getEnvironmentKey()).globalSystem;
	}

	/*
	 * Get the current environment hybrid time
	 */
	public HybridTime getEnvironmentHybridTime()
	{
		return globalSystem.environmentTime;
	}

	/*
	 * set a flag indicating that a jump is in progress
	 */
	public void setJumpOccurring(boolean jump)
	{
		globalSystem.jumpOccurring = jump;
	}

	/*
	 * Initializes the environment for
	 */
	public void initializeEnvironmentContent()
	{
		prepareComponents();
		initializeTimeDomains();
	}

	/*
	 * Initializes the time domain components of the environment
	 */
	public void initializeTimeDomains()
	{
		globalSystem.jumpOccurring = false;
		globalSystem.environmentTime = new HybridTime(true);
		globalSystem.jumpOccurring = false;
		globalSystem.earthStartTime = System.nanoTime() / 1000000000.0;
		getEnvironmentHybridTime().setJumpIndex(0);

	}

	/*
	 * Prepares all components for execution by linking the environment,
	 * intializing all components, and generating addresses
	 */
	void prepareComponents()
	{
		// this.initializeContentMappings(true);
		linkEnvironment();
		initializeComponents(Data.class, State.class);
		initializeComponents();
		// storeConfiguration();
		generateAddresses();
		linkEnvironment();
		// storeConfiguration();
	}

	/*
	 * Prepares a newly initialized environment
	 */
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

	/*
	 * Initializes components of the selected classes, or initializes all
	 * components if no classes are specified
	 * 
	 * @param components_to_initialize - component classes to be initialized
	 */
	private void initializeComponents(Class<?>... components_to_initialize)
	{
		List<Class<?>> initializeList = Arrays.asList(components_to_initialize);
		for (Component component : globalSystem.component().getContent().getComponents(true))
		{
			boolean initialize = initializeList.size() == 0;
			for (Class<?> checkClass : initializeList)
			{
				initialize = initialize || FieldFinder.containsSuper(component, checkClass);
			}
			if (initialize)
			{
				FullComponentOperator.getOperator(component).protectedInitialize();
				FullComponentOperator.getOperator(component).storeConfiguration();

			}
		}

		// storeConfiguration();
	}

	/*
	 * Generates and stores a unique address for each component
	 */
	private void generateAddresses()
	{
		for (Component component : globalSystem.component().getContent().getComponents(true))
		{

			FullComponentOperator.getOperator(component).generateAddress();
		}
	}

	/*
	 * Loads every component with the environment address to allow access to the
	 * environment from each component
	 */
	private void linkEnvironment()
	{
		this.initializeContentMappings();
		for (Component component : globalSystem.component().getContent().getComponents(true))
		{

			FullComponentOperator.getOperator(component)
			.setEnvironmentKey(FullComponentOperator.getOperator(globalSystem).getEnvironmentKey());
		}
	}

}
