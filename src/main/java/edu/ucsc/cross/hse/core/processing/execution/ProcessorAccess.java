package edu.ucsc.cross.hse.core.processing.execution;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.FullComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.environment.HybridEnvironment;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentOperator;
import edu.ucsc.cross.hse.core.procesing.io.FileExchanger;
import edu.ucsc.cross.hse.core.procesing.io.SystemConsole;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataHandler;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.event.ExecutionMonitor;
import edu.ucsc.cross.hse.core.processing.event.InterruptResponder;
import edu.ucsc.cross.hse.core.processing.event.JumpEvaluator;

/*
 * This class provides a connection to all of the processing elements making it
 * easier for them to coordinate. These Elements are all declared within the
 * central processor and are for operational usse only
 */
public abstract class ProcessorAccess
{

	protected EnvironmentManager processor; // environment interface
	private CentralProcessor proc; // central processor

	/*
	 * Constructor using the hybrid environment
	 */
	protected ProcessorAccess(EnvironmentManager processor)
	{
		this.processor = processor;
		this.proc = processor.processor;
	}

	/*
	 * Constructor using the central processor
	 */
	protected ProcessorAccess(CentralProcessor processor)
	{
		proc = processor;
		this.processor = processor.environmentInterface;
	}

	/*
	 * The following methods allow access to each of the processing modules, see
	 * central processor comments for details about each element
	 */
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

	public HybridEnvironment getEnv()
	{
		return processor.getEnvironment();
	}

	protected EnvironmentOperator getEnvironmentOperator()
	{
		return EnvironmentOperator.getOperator(getEnv().toString());
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

	protected ComponentDirector getComponents()
	{
		return proc.componentAdmin;
	}

	protected CentralProcessor getCenter()
	{
		return proc;
	}

	protected EnvironmentManager getProcessor()
	{
		// TODO Auto-generated method stub
		return proc.environmentInterface;
	}

	protected FileExchanger getFileParser()
	{
		return proc.fileExchanger;
	}

	protected FullComponentOperator getComponentOperator(Component component)
	{
		return FullComponentOperator.getOperator(component);
	}

	protected <S> DataOperator<S> getDataOperator(Data<S> component)
	{
		return DataOperator.getOperator(component);
	}

}
