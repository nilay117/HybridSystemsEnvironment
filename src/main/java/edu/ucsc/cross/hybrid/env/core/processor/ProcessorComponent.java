package edu.ucsc.cross.hybrid.env.core.processor;

import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hybrid.env.core.settings.SettingCollection;
import edu.ucsc.cross.hybrid.env.core.structure.EnvironmentContents;

public abstract class ProcessorComponent
{

	private Environment processor;

	protected ProcessorComponent(Environment processor)
	{
		this.processor = processor;
	}

	//@Override
	protected Time getEnvTime()
	{
		return processor.environment.time();
	}

	//@Override
	protected void setEnvTime(Time time)
	{
		// TODO Auto-generated method stub
		processor.environment.time().seconds(time.seconds());
	}

	//@Override
	protected SimulationEngine getComputationEngine()
	{
		return processor.simEngine;
	}

	//@Override
	protected ExecutionManager getEnvironmentMonitor()
	{
		return processor.simThread;
	}

	//@Override
	protected EnvironmentContents getEnvironment()
	{
		return processor.getEnvironment();
	}

	//@Override
	protected OutputConsole getConsole()
	{
		return processor.notifier;
	}

	protected SettingCollection getSettings()
	{
		return processor.getSettings();
	}

	protected DataCollector getData()
	{
		return processor.data;
	}

	protected ElementOperator getComponents()
	{
		return processor.elements;
	}

	//@Override
	protected Environment getProcessor()
	{
		// TODO Auto-generated method stub
		return processor;
	}

	protected IOParser getSaveUtility()
	{
		return processor.saver;
	}

	//@Override
	protected void setSettings(SettingCollection settings)
	{
		processor.setSettings(settings);
	}
}
