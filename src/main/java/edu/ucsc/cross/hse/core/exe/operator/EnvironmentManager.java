
package edu.ucsc.cross.hse.core.exe.operator;

import com.be3short.data.file.general.FileSystemInteractor;
import com.be3short.data.file.xml.XMLParser;
import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.exe.interfacing.ObjectInterface;
import edu.ucsc.cross.hse.core.exe.interfacing.SettingInterface;
import edu.ucsc.cross.hse.core.exe.interfacing.TimeInterface;
import edu.ucsc.cross.hse.core.exe.interfacing.VectorInterface;
import edu.ucsc.cross.hse.core.exe.monitor.Console;
import edu.ucsc.cross.hse.core.exe.monitor.EventMonitor;
import edu.ucsc.cross.hse.core.exe.monitor.ExecutionMonitor;
import edu.ucsc.cross.hse.core.obj.data.DataSet;
import edu.ucsc.cross.hse.core.obj.structure.EnvironmentContent;
import edu.ucsc.cross.hse.core.obj.structure.HybridTime;
import java.io.File;

@Loggable(Loggable.TRACE)
public class EnvironmentManager
{

	private SettingInterface settings;

	public void setSettings(SettingInterface settings)
	{
		this.settings = settings;
	}

	private EnvironmentContent contents;
	private SimulationEngine simEngine;
	private DataSet dataCollector;
	private EventMonitor jumpEvaluator;
	private ExecutionMonitor executionMonitor;
	private SystemOperator systemControl;
	private VectorInterface vector;
	private ObjectInterface objControl;
	private DataOperator dataManager;
	private TimeInterface timeOperator;
	private Console console;

	public DataOperator getDataManager()
	{
		return dataManager;
	}

	public SystemOperator getSystemControl()
	{
		return systemControl;
	}

	public VectorInterface getVector()
	{
		return vector;
	}

	public ObjectInterface getObjControl()
	{
		return objControl;
	}

	public void start()
	{
		prepare();
		dataManager.loadMap();
		console.initializeStatusPrintTimes();
		// manager.getSettings().getExecutionParameters().simulationDuration = run_time;

		getExecutionMonitor().launchEnvironment();

	}

	public void prepare()
	{
		try
		{
			resetData();
		} catch (Exception e)
		{

		}
		createNewEnvironment(false);
		objControl.prepareComponents();
		systemControl.clearChangeValues();
		// timeOperator.updateSimulationTime(new HybridTime(0.0, 0));
		vector.prepareVectors();
	}

	public EnvironmentManager()
	{
		createNewEnvironment();
	}

	private void createNewEnvironment()
	{
		createNewEnvironment(true);

	}

	public void createNewEnvironment(boolean new_contents)
	{
		if (new_contents)
		{
			settings = new SettingInterface();
			contents = new EnvironmentContent();
		}
		simEngine = new SimulationEngine(this);
		dataCollector = new DataSet(this);
		jumpEvaluator = new EventMonitor(this);
		executionMonitor = new ExecutionMonitor(this);
		dataManager = new DataOperator(this);
		systemControl = new SystemOperator(this);
		vector = new VectorInterface(this);
		objControl = new ObjectInterface(this);
		timeOperator = new TimeInterface(this);
		console = new Console(this);
	}

	public void resetData()
	{
		dataManager.restoreInitialData();
		dataCollector = new DataSet(this);
	}

	public EnvironmentContent getContents()
	{
		return contents;
	}

	public void setContents(EnvironmentContent contents)
	{
		this.contents = contents;
	}

	public SettingInterface getSettings()
	{
		return settings;
	}

	public SimulationEngine getSimEngine()
	{
		return simEngine;
	}

	public DataSet getDataCollector()
	{
		return dataCollector;
	}

	public EventMonitor getJumpEvaluator()
	{
		return jumpEvaluator;
	}

	public ExecutionMonitor getExecutionMonitor()
	{
		return executionMonitor;
	}

	public TimeInterface getTimeOperator()
	{
		return timeOperator;
	}

	public Console getConsole()
	{
		return console;
	}

	public static HybridEnvironment load(File file)
	{
		HybridEnvironment env = null;
		try
		{

			env = (HybridEnvironment) XMLParser.getObject(file);

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return env;
	}

	public void save(String file_path)
	{
		HybridEnvironment env = new HybridEnvironment();
		env.manager = this;
		save(file_path, env);
	}

	public void save(String file_path, HybridEnvironment env)
	{
		try
		{
			String outputXML = XMLParser.serializeObject(env);
			FileSystemInteractor.createOutputFile(new File(file_path), outputXML);

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
