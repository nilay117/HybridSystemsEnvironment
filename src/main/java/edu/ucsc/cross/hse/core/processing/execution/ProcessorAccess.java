package edu.ucsc.cross.hse.core.processing.execution;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystem;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystemOperator;
import edu.ucsc.cross.hse.core.procesing.io.FileParser;
import edu.ucsc.cross.hse.core.procesing.io.SystemConsole;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataManager;
import edu.ucsc.cross.hse.core.processing.event.EventMonitor;
import edu.ucsc.cross.hse.core.processing.settings.SettingConfigurations;

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

	// @Override
	protected Double getEnvTime()
	{
		return getEnvironmentOperator().getEnvironmentHybridTime().getTime();
	}

	// @Override
	protected void setEnvTime(Double time)
	{
		// TODO Auto-generated method stub
		getEnvironmentOperator().getEnvironmentHybridTime().setTime(time);
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
	protected GlobalSystemOperator getEnvironmentOperator()
	{
		return GlobalSystemOperator.getGlobalSystemOperator(getEnvironment().toString());
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

	protected DataManager getData()
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
