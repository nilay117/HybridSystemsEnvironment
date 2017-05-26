package edu.ucsc.cross.hse.core.component.system;

import bs.commons.objects.access.CoreComponent;
import bs.commons.unitvars.values.Time;
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

	@CoreComponent
	private HybridTime environmentTime; // time relative to when the environment
	@CoreComponent // was launched and jump index
	private Time earthStartTime;
	@CoreComponent
	private SettingConfigurations settings; // settings configuration
	@CoreComponent
	private boolean jumpOccurring; // flag indicating if a jump is
									// currently
	// occurring

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

	// Access Functions

	public SettingConfigurations getSettings()
	{
		return settings;
	}

	public HybridTime getEnvironmentTime()
	{
		return environmentTime;
	}

	public Time getStartTime()
	{
		return earthStartTime;
	}

	// Configration Functionsß
	public void setSettings(SettingConfigurations settings)
	{
		this.settings = settings;
	}

	private void initializeComponents()
	{
		jumpOccurring = false;
		environmentTime = new HybridTime();
		settings = SettingConfigurations.loadSettings();
		Component.setEnvironment(this, this.toString());
		GlobalAccessor.addGlobalHybridSystem(this);
	}

	@Override
	public void initialize()
	{
		// systems = new HashMap<String, GlobalHybridSystem>();
		// this.environmentKey = this.toString();
		// systems.put(environmentKey, this);
		jumpOccurring = false;
		earthStartTime = Time.newSecondsValue(System.nanoTime() / 1000000000.0);
	}

	public boolean isJumpOccurring()
	{
		return jumpOccurring;
	}

	public void setJumpOccurring(boolean jumpOccurring)
	{
		this.jumpOccurring = jumpOccurring;
	}
}
