package edu.ucsc.cross.hybrid.env.core.structure;

import java.util.ArrayList;

import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hybrid.env.core.components.HybridSystem;
import edu.ucsc.cross.hybrid.env.core.settings.SettingCollection;
import edu.ucsc.cross.hybrid.env.structural.BaseComponents;
import edu.ucsc.cross.hybrid.env.structural.Component;

public class EnvironmentContents extends Component
{

	private String environmentTitle;
	private Time startTime;
	private Integer jumpIndex;
	private ArrayList<HybridSystem> systems;
	private ArrayList<Component> allComponents;
	private Time envTime;
	private SettingCollection settings;

	public EnvironmentContents(String environment_title)
	{
		super(environment_title, BaseComponents.ENVIRONMENT);
		setEnvironmentTitle(environment_title);
		initializeDataStructures();
	}

	public EnvironmentContents()
	{
		super("Hybrid Systems Environment", BaseComponents.ENVIRONMENT);
		setEnvironmentTitle("Hybrid Environment");
		initializeDataStructures();
	}

	private void initializeDataStructures()
	{
		startTime = Time.newSecondsValue(0.0);
		settings = SettingCollection.getSettings();
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

	public SettingCollection getSettings()
	{
		return settings;
	}

	public void setSettings(SettingCollection settings)
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
