package edu.ucsc.cross.hse.core.processing.execution;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.framework.environment.ContentOperator;
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
		this.processor = processor.environmentInterface;
	}

	protected Double getEnvTime()
	{
		return getEnvironmentOperator().getEnvironmentHybridTime().getTime();
	}

	protected void setEnvTime(Double time)
	{
		// TODO Auto-generated method stub
		getEnvironmentOperator().getEnvironmentHybridTime().setTime(time);
	}

	protected SimulationEngine getComputationEngine()
	{
		return proc.simulationEngine;
	}

	protected ExecutionMonitor getEnvironmentMonitor()
	{
		return proc.executionMonitor;
	}

	protected InterruptResponder getInterruptHandler()
	{
		return proc.interruptResponder;
	}

	protected JumpEvaluator getJumpEvaluator()
	{
		return proc.jumpEvaluator;
	}

	public EnvironmentContent getEnv()
	{
		return processor.getContents();
	}

	protected ContentOperator getEnvironmentOperator()
	{
		return ContentOperator.getContentAdministrator(getEnv().toString());
	}

	protected SystemConsole getConsole()
	{
		return proc.systemConsole;
	}

	protected SettingConfigurer getSettings()
	{
		return processor.getSettings();
	}

	protected DataHandler getData()
	{
		return proc.dataHandler;
	}

	protected ComponentAdministrator getComponents()
	{
		return proc.componentAdmin;
	}

	protected CentralProcessor getCenter()
	{
		return proc;
	}

	protected HybridEnvironment getProcessor()
	{
		// TODO Auto-generated method stub
		return proc.environmentInterface;
	}

	protected FileExchanger getFileParser()
	{
		return proc.fileExchanger;
	}

	protected ComponentOperator getComponentOperator(Component component)
	{
		return ComponentOperator.getOperator(component);
	}

	protected <S> DataOperator<S> getDataOperator(Data<S> component)
	{
		return DataOperator.getOperator(component);
	}

	protected void setSettings(SettingConfigurer settings)
	{
		proc.environmentInterface.loadSettings(settings);
	}
}
