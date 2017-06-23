package edu.ucsc.cross.hse.core.processing.execution;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.environment.GlobalEnvironmentContent;
import edu.ucsc.cross.hse.core.framework.environment.GlobalContentAdministrator;
import edu.ucsc.cross.hse.core.procesing.io.FileExchanger;
import edu.ucsc.cross.hse.core.procesing.io.SystemConsole;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataHandler;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.event.ExecutionMonitor;
import edu.ucsc.cross.hse.core.processing.event.InterruptResponder;
import edu.ucsc.cross.hse.core.processing.event.JumpEvaluator;

public abstract class ProcessingElement
{

	protected HybridEnvironment processor; // central processors
	private CentralProcessor proc;

	protected ProcessingElement(HybridEnvironment processor)
	{
		this.processor = processor;
		this.proc = processor.processor;
	}

	protected ProcessingElement(CentralProcessor processor)
	{
		proc = processor;
		this.processor = processor.environment;
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
	protected ExecutionMonitor getEnvironmentMonitor()
	{
		return proc.executionMonitor;
	}

	protected InterruptResponder getInterruptHandler()
	{
		return proc.terminator;
	}

	protected JumpEvaluator getJumpEvaluator()
	{
		return proc.jumpHandler;
	}

	// @Override
	public GlobalEnvironmentContent getEnv()
	{
		return processor.getContents();
	}

	// @Override
	protected GlobalContentAdministrator getEnvironmentOperator()
	{
		return GlobalContentAdministrator.getContentAdministrator(getEnv().toString());
	}

	// @Override
	protected SystemConsole getConsole()
	{
		return proc.outputPrinter;
	}

	protected SettingConfigurer getSettings()
	{
		return processor.getSettings();
	}

	protected DataHandler getData()
	{
		return proc.data;
	}

	protected ComponentAdministrator getComponents()
	{
		return proc.elements;
	}

	// @Override
	protected HybridEnvironment getProcessor()
	{
		// TODO Auto-generated method stub
		return proc.environment;
	}

	protected FileExchanger getFileParser()
	{
		return proc.fileParser;
	}

	protected ComponentOperator getComponentOperator(Component component)
	{
		return ComponentOperator.getConfigurer(component);
	}

	protected <S> DataOperator<S> getDataOperator(Data<S> component)
	{
		return DataOperator.getOperator(component);
	}

	// @Override
	protected void setSettings(SettingConfigurer settings)
	{
		proc.environment.loadSettings(settings);
	}
}
