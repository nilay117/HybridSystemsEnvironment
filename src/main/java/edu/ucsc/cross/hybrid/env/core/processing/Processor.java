package edu.ucsc.cross.hybrid.env.core.processing;

import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hybrid.env.core.containers.EnvironmentContent;

public abstract class Processor
{

	private Environment processor;

	protected Processor(Environment processor)
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
	protected ExecutionMonitor getEnvironmentMonitor()
	{
		return processor.simThread;
	}

	//@Override
	protected EnvironmentContent getEnvironment()
	{
		return processor.getEnvironment();
	}

	//@Override
	protected OutputPrinter getConsole()
	{
		return processor.notifier;
	}

	protected SettingConfigurer getSettings()
	{
		return processor.getSettings();
	}

	protected DataCollector getData()
	{
		return processor.data;
	}

	protected ElementManager getComponents()
	{
		return processor.elements;
	}

	//@Override
	protected Environment getProcessor()
	{
		// TODO Auto-generated method stub
		return processor;
	}

	protected FileParser getSaveUtility()
	{
		return processor.saver;
	}

	//@Override
	protected void setSettings(SettingConfigurer settings)
	{
		processor.setSettings(settings);
	}
}
