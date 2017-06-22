package edu.ucsc.cross.hse.core.framework.component;

import java.io.File;

import bs.commons.objects.execution.Initializer;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.environment.GlobalEnvironmentContent;
import edu.ucsc.cross.hse.core.framework.environment.GlobalContentAdministrator;
import edu.ucsc.cross.hse.core2.framework.component.ComponentAddress;
import edu.ucsc.cross.hse.core2.framework.environment.GlobalSystemInterface;

/*
 * This class is the foundation of all components to ensures proper
 * compatibility. Anything that is an extension of this class can be used with
 * the hybrid systems environment as standalone components or pieces of another
 * component. The hierarchical structure allows these extensions to be accessed
 * regardless of location, ie in a HashMap within a sub component. This class
 * also provides some additional functionality and access to the environment and
 * components within its hierarchy.
 */
public abstract class Component implements Initializer
{

	ComponentStatus state; // configuration state of
							// this instance of the
							// component

	ComponentInformation description; // specific information
										// describing
										// the component

	ComponentOrganizer hierarchy; // component access hierarchy of
									// this
									// component
	/*
	 * Constructor that defines the name of the component with this class
	 * (Component) as the base class. This constructor is used to create
	 * components that are not based off any of the core components:
	 * Behavior,Data,DataSet, and HybridSystem.
	 * 
	 * @param title - title of the component
	 */

	public Component(String title, String description)
	{
		getAdministrator().setup(title, this.getClass());
		this.description.setDescription(description);

	}

	/*
	 * Constructor that defines the name of the component with this class
	 * (Component) as the base class. This constructor is used to create
	 * components that are not based off any of the core components:
	 * Behavior,Data,DataSet, and HybridSystem.
	 * 
	 * @param title - title of the component
	 */
	public Component(String title)
	{
		getAdministrator().setup(title, this.getClass());

	}

	/*
	 * Constructor that defines the name of the component with this class
	 * (Component) as the base class. This constructor is used to create
	 * components that are not based off any of the core components:
	 * Behavior,Data,DataSet, and HybridSystem.
	 * 
	 * @param title - title of the component
	 */
	public Component()
	{
		getAdministrator().setup(this.getClass().getSimpleName(), this.getClass());

	}

	/*
	 * User Access Functions
	 */

	/*
	 * Accesses the global environment containing all other components and the
	 * time domains
	 * 
	 * @return global environment component
	 */
	public GlobalEnvironmentContent getEnvironment()
	{
		return GlobalContentAdministrator.getGlobalSystem(this);
	}

	/*
	 * Accesses information about this component for naming conventions
	 * 
	 * @return component information
	 */
	public ComponentInformation getInformation()
	{
		return description;
	}

	/*
	 * Accesses an organized data structure for accessing objects within this
	 * components hierarchy
	 * 
	 * @return component organizer
	 */
	public ComponentOrganizer getContents()
	{
		return hierarchy;
	}

	/*
	 * Accesses a set of actions that are user friendly, meaning they will not
	 * interfere with the functionality of the environment
	 * 
	 * @return component operator
	 */
	public ComponentOperator getActions()
	{
		return ComponentAdministrator.getConfigurer(this);
	}

	/*
	 * Initialize method that can be redefined and will be called each time the
	 * environment starts. This method is intentionally empty to save space and
	 * time as many components do not require it.
	 */
	@Override
	public void initialize()
	{

	}

	/////////////////////////////////////////////////////////////////////////
	/// Development & Processing Components (not needed for regular use) ////
	/////////////////////////////////////////////////////////////////////////

	/*
	 * Accesses the current status of the component. This is for the processor
	 * and is not intended for users.
	 */
	ComponentStatus getStatus()
	{
		return state;
	}

	/*
	 * Accesses an expanded set of actions that are used by various modules
	 * within the environment. These actions are for processing tasks only and
	 * can cause problems within the environment if used incorrectly.
	 * 
	 * @return component administrator
	 */
	ComponentAdministrator getAdministrator()
	{
		return ComponentAdministrator.getConfigurer(this);
	}

}
