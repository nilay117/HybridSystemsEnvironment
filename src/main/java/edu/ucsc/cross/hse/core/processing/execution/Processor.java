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

public class Processor
{

	protected Environment environment;
	protected ComponentDirector elements;
	protected DataCollector data;
	protected SimulationEngine simulationEngine;
	protected EventMonitor executionMonitor;
	protected FileParser fileParser;
	protected SystemConsole outputPrinter; // system notification manager ie % complet

	protected Processor(Environment processor)
	{
		environment = processor;
		initializeComponents();
	}

	private void initializeComponents()
	{
		simulationEngine = new SimulationEngine(this);
		executionMonitor = new EventMonitor(this);
		data = new DataCollector(this);
		outputPrinter = new SystemConsole(this);
		elements = new ComponentDirector(this);
		fileParser = new FileParser(this);
	}

	//	// @Override
	//	protected Double getEnvTime()
	//	{
	//		return environment.getEnvironmentContent().getEnvironmentTime().getTime();
	//	}
	//
	//	// @Override
	//	protected void setEnvTime(Double time)
	//	{
	//		// TODO Auto-generated method stub
	//		environment.getEnvironmentContent().getEnvironmentTime().setTime(time);
	//	}
	//
	//	// @Override
	//	protected SimulationEngine getComputationEngine()
	//	{
	//		return simulationEngine;
	//	}
	//
	//	// @Override
	//	protected EventMonitor getEnvironmentMonitor()
	//	{
	//		return executionMonitor;
	//	}
	//
	//	// @Override
	//	protected GlobalSystem getEnvironment()
	//	{
	//		return environment.getEnvironmentContent();
	//	}
	//
	//	// @Override
	//	protected SystemConsole getConsole()
	//	{
	//		return outputPrinter;
	//	}
	//
	//	protected SettingConfigurations getSettings()
	//	{
	//		return getSettings();
	//	}
	//
	//	protected DataCollector getData()
	//	{
	//		return data;
	//	}
	//
	//	protected ComponentDirector getComponents()
	//	{
	//		return elements;
	//	}
	//
	//	// @Override
	//	protected Environment getProcessor()
	//	{
	//		// TODO Auto-generated method stub
	//		return environment;
	//	}
	//
	//	protected FileParser getSaveUtility()
	//	{
	//		return fileParser;
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

	// @Override
	protected void setSettings(SettingConfigurations settings)
	{
		environment.setSettings(settings);
	}

	protected void prepareEnvironment()
	{
		// environment.scanAllSystems();
		elements.prepareComponents();
		simulationEngine.initialize();
		data.loadStoreStates();
	}

	public void start()
	{
		prepareEnvironment();
		executionMonitor.runSim(false);
	}
}
