package edu.ucsc.cross.hybrid.env.core.structure;

import java.util.ArrayList;

import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hybrid.env.core.components.HybridSystem;
import edu.ucsc.cross.hybrid.env.core.settings.Settings;

public class EnvironmentElements extends Component
{

	private String environmentTitle;
	private Time startTime;
	private ArrayList<HybridSystem> systems;
	private ArrayList<Component> allComponents;
	private Time envTime;
	private Settings settings;

	public EnvironmentElements(String environment_title)
	{
		super(environment_title, ComponentClassification.ENVIRONMENT);
		setEnvironmentTitle(environment_title);
		initializeDataStructures();
	}

	public EnvironmentElements()
	{
		super("Hybrid Systems Environment", ComponentClassification.ENVIRONMENT);
		setEnvironmentTitle("Hybrid Environment");
		initializeDataStructures();
	}

	private void initializeDataStructures()
	{
		startTime = Time.newSecondsValue(0.0);
		settings = Settings.getSettings();
		envTime = Time.newSecondsValue(0.0);
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

	public ArrayList<HybridSystem> getAllSystems()
	{
		return systems;
	}

	public ArrayList<Component> getAllComponents()
	{
		return allComponents;
	}

	public Time getEnvTime()
	{
		return envTime;
	}

	public Settings getSettings()
	{
		return settings;
	}

	public void setSettings(Settings settings)
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
