package edu.ucsc.cross.hse.core.processing.management;

import edu.ucsc.cross.hse.core.component.data.Data;
import edu.ucsc.cross.hse.core.component.data.DataOperator;
import edu.ucsc.cross.hse.core.component.foundation.Component;
import edu.ucsc.cross.hse.core.component.foundation.ComponentOperator;
import edu.ucsc.cross.hse.core.component.system.GlobalHybridSystem;
import edu.ucsc.cross.hse.core.object.settings.SettingConfigurations;
import edu.ucsc.cross.hse.core.procesing.output.SystemConsole;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataCollector;
import edu.ucsc.cross.hse.core.processing.data.FileParser;
import edu.ucsc.cross.hse.core.processing.event.ExecutionMonitor;

public abstract class ProcessorAccess
{

	protected Environment processor; // central processors

	protected ProcessorAccess(Environment processor)
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
	protected GlobalHybridSystem getEnvironment()
	{
		return processor.environmentContent;
	}

	// @Override
	protected SystemConsole getConsole()
	{
		return processor.outputPrinter;
	}

	protected SettingConfigurations getSettings()
	{
		return processor.getEnvironmentContent().getSettings();
	}

	protected DataCollector getData()
	{
		return processor.data;
	}

	protected ComponentDirector getComponents()
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

	protected ComponentOperator compOps(Component component)
	{
		return ComponentOperator.getConfigurer(component);
	}

	protected DataOperator dataOps(Data component)
	{
		return DataOperator.dataOp(component);
	}

	// @Override
	protected void setSettings(SettingConfigurations settings)
	{
		processor.setSettings(settings);
	}
}
