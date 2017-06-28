package edu.ucsc.cross.hse.core.framework.component;

import java.io.File;

import bs.commons.objects.execution.Initializer;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.execution.HybridEnvironment;
import edu.ucsc.cross.hse.core.framework.environment.ContentOperator;
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

	ComponentStatus status; // current status of this component

	ComponentLabel labels; // specific information describing this component

	ComponentOrganizer contents; // component access hierarchy of this component

	/*
	 * Constructor that defines the name of the component with this class
	 * (Component) as the base class. This constructor is used to create
	 * components that are not based off any of the core components:
	 * Behavior,Data,DataSet, and HybridSystem.
	 * 
	 * @param classification - general title of the component
	 * 
	 * @param name - specific name of the component
	 */
	public Component(String classification, String name)
	{
		ComponentOperator.getOperator(this).setup(classification, this.getClass());
		this.labels.setName(name);

	}

	/*
	 * Constructor that defines the name of the component with this class
	 * (Component) as the base class. This constructor is used to create
	 * components that are not based off any of the core components:
	 * Behavior,Data,DataSet, and HybridSystem.
	 * 
	 * @param classification - general title of the component
	 */
	public Component(String classification)
	{
		ComponentOperator.getOperator(this).setup(classification, this.getClass());

	}

	/*
	 * Constructor that defines the name of the component with this class
	 * (Component) as the base class. This constructor is used to create
	 * components that are not based off any of the core components:
	 * Behavior,Data,DataSet, and HybridSystem.
	 * 
	 */
	public Component()
	{
		ComponentOperator.getOperator(this).setup(this.getClass().getSimpleName(), this.getClass());

	}

	/*
	 * Accesses the global environment containing all other components and the
	 * time domains
	 * 
	 * @return global environment component
	 */
	public EnvironmentContent getEnvironment()
	{
		return ContentOperator.getGlobalSystem(this);
	}

	/*
	 * Accesses information about this component for naming conventions
	 * 
	 * @return component information
	 */
	public ComponentLabel getLabels()
	{
		return labels;
	}

	/*
	 * Accesses an organized data structure for accessing objects within this
	 * components hierarchy
	 * 
	 * @return component organizer
	 */
	public ComponentOrganizer getContents()
	{
		return contents;
	}

	/*
	 * Accesses a set of actions that are user friendly, meaning they will not
	 * interfere with the functionality of the environment
	 * 
	 * @return component operator
	 */
	public ComponentWorker getActions()
	{
		return ComponentOperator.getOperator(this);
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

	public SettingConfigurer getSettings()
	{
		return HybridEnvironment.getEnvironment(contents.getEnvironmentKey()).getSettings();
	}
}
