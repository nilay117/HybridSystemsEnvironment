package edu.ucsc.cross.hybrid.env.core.containers;

import java.util.ArrayList;

import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hybrid.env.core.components.Component;
import edu.ucsc.cross.hybrid.env.core.components.HybridSystem;
import edu.ucsc.cross.hybrid.env.core.definitions.CoreComponent;
import edu.ucsc.cross.hybrid.env.core.processing.SettingConfigurer;

public class EnvironmentContent extends Component
{

	private String environmentTitle;
	private Time startTime;
	private Integer jumpIndex;
	private ArrayList<HybridSystem> systems;
	private ArrayList<Component> allComponents;
	private Time envTime;
	private SettingConfigurer settings;

	public EnvironmentContent(String environment_title)
	{
		super(environment_title, CoreComponent.ENVIRONMENT);
		setEnvironmentTitle(environment_title);
		initializeDataStructures();
	}

	public EnvironmentContent()
	{
		super("Hybrid Systems Environment", CoreComponent.ENVIRONMENT);
		setEnvironmentTitle("Hybrid Environment");
		initializeDataStructures();
	}

	private void initializeDataStructures()
	{
		startTime = Time.newSecondsValue(0.0);
		settings = SettingConfigurer.getSettings();
		envTime = Time.newSecondsValue(0.0);
		jumpIndex = 0;
		systems = new ArrayList<HybridSystem>();
		allComponents = new ArrayList<Component>();
	}

	public <T extends HybridSystem> void addSystem(T component)
	{
		//systems.add(component);
		addSystem(component, 1);
	}

	@SuppressWarnings("unchecked")
	public <T extends HybridSystem> void addSystem(T component, Integer quantity)
	{
		T componentToClone = component;
		for (Integer ind = 0; ind < quantity; ind++)
		{
			T clonedComponent = (T) ObjectCloner.xmlClone(componentToClone);
			if (!systems.contains(clonedComponent))
			{
				systems.add(clonedComponent);
			}
			componentToClone = clonedComponent;
		}
	}

	public <T extends Component> void addComponent(T component)
	{
		addComponent(component, 1);
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> void addComponent(T component, Integer quantity)
	{
		T initialClone = (T) ObjectCloner.xmlClone(component);
		for (Integer ind = 0; ind < quantity; ind++)
		{
			T clonedComponent = (T) ObjectCloner.xmlClone(initialClone);
			storeComponent(clonedComponent, true);
		}
	}

	public ArrayList<HybridSystem> getAllSystems()
	{
		return systems;
	}

	public ArrayList<Component> getAllComponents()
	{
		return allComponents;
	}

	public Time time()
	{
		return envTime;
	}

	public Integer incrementJumpIndex()
	{

		return incrementJumpIndex(1);
	}

	public Integer incrementJumpIndex(Integer quantity)
	{
		jumpIndex = jumpIndex + quantity;
		return jumpIndex;
	}

	public SettingConfigurer getSettings()
	{
		return settings;
	}

	public void setSettings(SettingConfigurer settings)
	{
		this.settings = settings;
	}

	public Time getStartTime()
	{
		return startTime;
	}

	public String getEnvironmentTitle()
	{
		return environmentTitle;
	}

	public void setEnvironmentTitle(String environmentTitle)
	{
		this.environmentTitle = environmentTitle;
	}

	@Override
	public void initialize()
	{
		// TODO Auto-generated method stub

	}

}
