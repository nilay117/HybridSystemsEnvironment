package edu.ucsc.cross.hybrid.env.core.elements;

import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hybrid.env.core.constructors.Component;
import edu.ucsc.cross.hybrid.env.core.constructors.HybridSystem;
import edu.ucsc.cross.hybrid.env.core.domains.HybridTime;
import edu.ucsc.cross.hybrid.env.core.settings.SettingConfiguration;

public class GlobalContent extends HybridSystem
{

	private String environmentTitle;
	private Time startTime;
	private Integer jumpIndex;
	private Time envTime;
	private HybridTime environmentTime;
	private SettingConfiguration settings;

	public GlobalContent(String environment_title)
	{
		super(environment_title);
		setEnvironmentTitle(environment_title);
		initializeComponents();
	}

	public GlobalContent()
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
		settings = SettingConfiguration.loadSettings();
		envTime = Time.newSecondsValue(0.0);
		jumpIndex = 0;

	}

	public SettingConfiguration getSettings()
	{
		return settings;
	}

	public void setSettings(SettingConfiguration settings)
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
