package edu.ucsc.cross.hse.core.processing.execution;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystem;
import edu.ucsc.cross.hse.core.object.settings.SettingConfigurations;
import edu.ucsc.cross.hse.core.procesing.output.SystemConsole;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataCollector;
import edu.ucsc.cross.hse.core.processing.data.FileParser;
import edu.ucsc.cross.hse.core.processing.event.EventMonitor;

public abstract class ProcessorAccess
{

	protected Environment processor; // central processors
	private Processor proc;

	protected ProcessorAccess(Environment processor)
	{
		this.processor = processor;
	}

	protected ProcessorAccess(Processor processor)
	{
		proc = processor;
	}

	//	// @Override
	//	protected Double getEnvTime()
	//	{
	//		return getEnvironment().getEnvironmentTime().getTime();
	//	}
	//
	//	// @Override
	//	protected void setEnvTime(Double time)
	//	{
	//		// TODO Auto-generated method stub
	//		processor.environmentContent.getEnvironmentTime().setTime(time);
	//	}
	//
	//	// @Override
	//	protected SimulationEngine getComputationEngine()
	//	{
	//		return processor.simulationEngine;
	//	}
	//
	//	// @Override
	//	protected ExecutionMonitor getEnvironmentMonitor()
	//	{
	//		return processor.executionMonitor;
	//	}
	//
	//	// @Override
	//	protected GlobalSystem getEnvironment()
	//	{
	//		return processor.environmentContent;
	//	}
	//
	//	// @Override
	//	protected SystemConsole getConsole()
	//	{
	//		return processor.outputPrinter;
	//	}
	//
	//	protected SettingConfigurations getSettings()
	//	{
	//		return processor.getSettings();
	//	}
	//
	//	protected DataCollector getData()
	//	{
	//		return processor.data;
	//	}
	//
	//	protected ComponentDirector getComponents()
	//	{
	//		return processor.elements;
	//	}
	//
	//	// @Override
	//	protected Environment getProcessor()
	//	{
	//		// TODO Auto-generated method stub
	//		return processor;
	//	}
	//
	//	protected FileParser getSaveUtility()
	//	{
	//		return processor.fileParser;
	//	}
	//
	//	protected ComponentOperator compOps(Component component)
	//	{
	//		return ComponentOperator.getConfigurer(component);
	//	}
	//
	//	protected DataOperator dataOps(Data component)
	//	{
	//		return DataOperator.dataOp(component);
	//	}
	//
	//	// @Override
	//	protected void setSettings(SettingConfigurations settings)
	//	{
	//		processor.setSettings(settings);
	//	}
	//	

	// @Override
	protected Double getEnvTime()
	{
		return proc.environment.getEnvironmentContent().getEnvironmentTime().getTime();
	}

	// @Override
	protected void setEnvTime(Double time)
	{
		// TODO Auto-generated method stub
		proc.environment.getEnvironmentContent().getEnvironmentTime().setTime(time);
	}

	// @Override
	protected SimulationEngine getComputationEngine()
	{
		return proc.simulationEngine;
	}

	// @Override
	protected EventMonitor getEnvironmentMonitor()
	{
		return proc.executionMonitor;
	}

	// @Override
	protected GlobalSystem getEnvironment()
	{
		return proc.environment.getEnvironmentContent();
	}

	// @Override
	protected SystemConsole getConsole()
	{
		return proc.outputPrinter;
	}

	protected SettingConfigurations getSettings()
	{
		return proc.environment.settings();
	}

	protected DataCollector getData()
	{
		return proc.data;
	}

	protected ComponentDirector getComponents()
	{
		return proc.elements;
	}

	// @Override
	protected Environment getProcessor()
	{
		// TODO Auto-generated method stub
		return proc.environment;
	}

	protected FileParser getSaveUtility()
	{
		return proc.fileParser;
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
		proc.environment.setSettings(settings);
	}
}
