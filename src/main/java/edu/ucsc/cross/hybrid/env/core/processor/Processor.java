package edu.ucsc.cross.hybrid.env.core.processor;

import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hybrid.env.core.settings.Settings;
import edu.ucsc.cross.hybrid.env.core.structure.EnvironmentElements;

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
		return processor.environment.getEnvTime();
	}

	//@Override
	protected void setEnvTime(Time time)
	{
		// TODO Auto-generated method stub
		processor.environment.getEnvTime().seconds(time.seconds());
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
	protected EnvironmentElements getEnvironment()
	{
		return processor.environment;
	}

	//@Override
	protected OutputConsole getConsole()
	{
		return processor.notifier;
	}

	protected Settings getSettings()
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
	protected void setSettings(Settings settings)
	{
		processor.setSettings(settings);
	}
}
