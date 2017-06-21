package edu.ucsc.cross.hse.core.processing.execution;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentAdministrator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataAdministrator;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContentOperator;
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
	protected EnvironmentContent getEnvironment()
	{
		return proc.environment.getEnvironmentContent();
	}

	// @Override
	protected EnvironmentContentOperator getEnvironmentOperator()
	{
		return EnvironmentContentOperator.getGlobalSystemOperator(getEnvironment().toString());
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

	protected FileParser getFileParser()
	{
		return proc.fileParser;
	}

	protected ComponentAdministrator getComponentOperator(Component component)
	{
		return ComponentAdministrator.getConfigurer(component);
	}

	protected <S> DataAdministrator<S> getDataOperator(Data<S> component)
	{
		return DataAdministrator.getOperator(component);
	}

	// @Override
	protected void setSettings(SettingConfigurations settings)
	{
		proc.environment.setSettings(settings);
	}
}
