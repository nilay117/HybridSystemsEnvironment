package edu.ucsc.cross.hse.core.processing.management;

import edu.ucsc.cross.hse.core.object.containers.GlobalEnvironmentContents;
import edu.ucsc.cross.hse.core.object.containers.SettingConfigurations;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataCollector;
import edu.ucsc.cross.hse.core.processing.data.FileParser;
import edu.ucsc.cross.hse.core.processing.data.SystemConsole;
import edu.ucsc.cross.hse.core.processing.event.ExecutionMonitor;

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
	protected GlobalEnvironmentContents getEnvironment()
	{
		return processor.environmentContent();
	}

	// @Override
	protected SystemConsole getConsole()
	{
		return processor.outputPrinter;
	}

	protected SettingConfigurations getSettings()
	{
		return processor.getSettings();
	}

	protected DataCollector getData()
	{
		return processor.data;
	}

	protected ComponentOperator getComponents()
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
	protected void setSettings(SettingConfigurations settings)
	{
		processor.setSettings(settings);
	}
}
