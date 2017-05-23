package edu.ucsc.cross.hse.core.component.system;

import edu.ucsc.cross.hse.core.component.constructors.Component;
import edu.ucsc.cross.hse.core.component.constructors.HybridSystem;
import edu.ucsc.cross.hse.core.object.domains.HybridTime;
import edu.ucsc.cross.hse.core.object.settings.SettingConfigurations;

/*
 * This is the main root system of the environment that contains all of the
 * components. This system also contains the setting configurations to be easily
 * accessed by the processor and any environment components, and also so that
 * they will be saved when this class is exportated.
 */
public class GlobalHybridSystem extends HybridSystem
{

	private HybridTime environmentTime; // time relative to when the environment
										// was launched and jump index
	private SettingConfigurations settings; // settings configuration

	public GlobalHybridSystem()
	{
		super("Global Environment Hybrid Systems");
		initializeComponents();
	}

	public GlobalHybridSystem(String environment_title)
	{
		super(environment_title);
		initializeComponents();
	}

	public HybridTime getEnvironmentTime()
	{
		return environmentTime;
	}

	public SettingConfigurations getSettings()
	{
		return settings;
	}

	public void setSettings(SettingConfigurations settings)
	{
		this.settings = settings;
	}

	@Override
	public void initialize()
	{
		// TODO Auto-generated method stub
	}

	private void initializeComponents()
	{
		environmentTime = new HybridTime();
		settings = SettingConfigurations.loadSettings();
		Component.setEnvironment(this, this);
	}

}
