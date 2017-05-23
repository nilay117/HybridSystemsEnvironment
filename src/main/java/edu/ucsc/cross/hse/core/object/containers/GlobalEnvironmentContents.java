package edu.ucsc.cross.hse.core.object.containers;

import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.component.constructors.Component;
import edu.ucsc.cross.hse.core.component.constructors.HybridSystem;
import edu.ucsc.cross.hse.core.object.domains.HybridTime;

public class GlobalEnvironmentContents extends HybridSystem
{

	private String environmentTitle;
	private Time startTime;
	private Integer jumpIndex;
	private Time envTime;
	private HybridTime environmentTime;
	private SettingConfigurations settings;

	public GlobalEnvironmentContents(String environment_title)
	{
		super(environment_title);
		setEnvironmentTitle(environment_title);
		initializeComponents();
	}

	public GlobalEnvironmentContents()
	{
		super("Hybrid Systems Environment");
		initializeComponents();
	}

	private void initializeComponents()
	{
		environmentTime = new HybridTime();
		setEnvironmentTitle("Hybrid Environment");
		initializeDataStructures();
		Component.setEnvironment(this, this);
	}

	private void initializeDataStructures()
	{
		startTime = Time.newSecondsValue(0.0);
		settings = SettingConfigurations.loadSettings();
		envTime = Time.newSecondsValue(0.0);
		jumpIndex = 0;

	}

	public SettingConfigurations getSettings()
	{
		return settings;
	}

	public void setSettings(SettingConfigurations settings)
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

	public HybridTime getEnvironmentTime()
	{
		return environmentTime;
	}

}
