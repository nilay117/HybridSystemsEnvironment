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

/*
 * This class is just an interface between all of the processing elements making
 * it easier for them to coordinate. These Elements are all declared within the
 * central processor and are for operational usse only
 */
public abstract class ProcessingElement
{

	protected HybridEnvironment processor; // environment interface
	private CentralProcessor proc; // central process or

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

	protected SimulationEngine getComputationEngine()
	{
		return proc.simulationEngine;
	}

	protected ExecutionMonitor getEnvironmentMonitor()
	{
		return proc.integrationMonitor;
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
		return processor.getE();
	}

	protected ContentOperator getEnvironmentOperator()
	{
		return ContentOperator.getOperator(getEnv().toString());
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
		proc.environmentInterface.settings.loadSettings(settings);
	}
}
