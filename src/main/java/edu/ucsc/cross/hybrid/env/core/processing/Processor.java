package edu.ucsc.cross.hybrid.env.core.processing;

import edu.ucsc.cross.hybrid.env.core.elements.GlobalContent;
import edu.ucsc.cross.hybrid.env.core.settings.SettingConfiguration;

public abstract class Processor
{

	private Environment processor;

	protected Processor(Environment processor)
	{
		this.processor = processor;
	}

	// @Override
	protected Double getEnvTime()
	{
		return getEnvironment().getEnvironmentTime().getTime();
	}

	// @Override
	protected void setEnvTime(Double time)
	{
		// TODO Auto-generated method stub
		processor.environmentContent.getEnvironmentTime().setTime(time);
	}

	// @Override
	protected SimulationEngine getComputationEngine()
	{
		return processor.simulationEngine;
	}

	// @Override
	protected ExecutionMonitor getEnvironmentMonitor()
	{
		return processor.executionMonitor;
	}

	// @Override
	protected GlobalContent getEnvironment()
	{
		return processor.environmentContent();
	}

	// @Override
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

	protected ComponentManager getComponents()
	{
		return processor.elements;
	}

	// @Override
	protected Environment getProcessor()
	{
		// TODO Auto-generated method stub
		return processor;
	}

	protected FileParser getSaveUtility()
	{
		return processor.fileParser;
	}

	// @Override
	protected void setSettings(SettingConfiguration settings)
	{
		processor.setSettings(settings);
	}
}
