package edu.ucsc.cross.hybrid.env.core.processing;

import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hybrid.env.core.containers.EnvironmentContent;
import edu.ucsc.cross.hybrid.env.core.containers.SettingConfiguration;

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
		return processor.environmentContent.environmentTime();
	}

	//@Override
	protected void setEnvTime(Time time)
	{
		// TODO Auto-generated method stub
		processor.environmentContent.environmentTime().seconds(time.seconds());
	}

	//@Override
	protected SimulationEngine getComputationEngine()
	{
		return processor.simulationEngine;
	}

	//@Override
	protected ExecutionMonitor getEnvironmentMonitor()
	{
		return processor.executionMonitor;
	}

	//@Override
	protected EnvironmentContent getEnvironment()
	{
		return processor.environmentContent();
	}

	//@Override
	protected SystemConsole getConsole()
	{
		return processor.outputPrinter;
	}

	protected SettingConfiguration getSettings()
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
		return processor.fileParser;
	}

	//@Override
	protected void setSettings(SettingConfiguration settings)
	{
		processor.setSettings(settings);
	}
}
