package edu.ucsc.cross.hse.core.exe.operator;

import bs.commons.objects.manipulation.ObjectCloner;
import com.be3short.data.compression.DataDecompressor;
import com.be3short.data.file.general.FileSystemInteractor;
import com.be3short.data.file.xml.XMLParser;
import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.exe.interfacing.SettingInterface;
import edu.ucsc.cross.hse.core.obj.config.ExecutionParameters;
import edu.ucsc.cross.hse.core.obj.structure.EnvironmentContent;
import edu.ucsc.cross.hse.core.obj.structure.HybridSystem;
import edu.ucsc.cross.hse.tools.ui.resultview.PlotGenerator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.GZIPOutputStream;
import javafx.application.Application;
import javafx.stage.Stage;

/*
 * The main class of the hybrid systeme environment, conting the manager, contents, and the setting configurer.
 */
@Loggable(Loggable.TRACE)
public class HybridEnvironment extends Application
{

	public EnvironmentManager manager;

	public HybridEnvironment()
	{
		manager = new EnvironmentManager();
	}

	public void start(Double run_time)
	{
		manager.getSettings().getExecutionParameters().simulationDuration = run_time;
		start();
	}

	public void start(Double run_time, Integer jump_limit)
	{
		manager.getSettings().getExecutionParameters().simulationDuration = run_time;
		manager.getSettings().getExecutionParameters().maximumJumps = jump_limit;
		start();
	}

	public void start()
	{
		manager.start();

	}

	public void start(ExecutionParameters params)
	{
		manager.getSettings().setExecutionParameters(params);
		// manager.getContentManager().loadWorld();
		manager.getExecutionMonitor().launchEnvironment();
	}

	public SettingInterface getSettings()
	{
		return manager.getSettings();
	}

	public EnvironmentContent getContents()
	{
		return manager.getContents();
	}

	public void loadNewEnv(HybridEnvironment env)
	{
		manager.setSettings(getManager(env).getSettings());
		manager.setContents(getManager(env).getContents());
		// manager.createNewEnvironment(false);
	}

	public void addContent(HybridSystem<?>... systems)
	{
		for (HybridSystem<?> system : systems)
		{
			manager.getContents().addSystems(system);
		}
	}

	public void addContent(HybridSystem<?> system, Integer quantity)
	{
		addContent(system);
		for (Integer i = 1; i < quantity; i++)
		{
			HybridSystem<?> copy = system.getClass().cast(ObjectCloner.xmlClone(system));
			addContent(copy);
		}
	}

	public void save(String file_path)
	{
		manager.save(file_path, this);
	}

	public void loadContent(File file)
	{
		HybridEnvironment env = EnvironmentManager.load(file);
		manager = env.manager;
	}

	public static HybridEnvironment load(File file)
	{
		HybridEnvironment env = EnvironmentManager.load(file);
		return env;
	}

	public static EnvironmentManager getManager(HybridEnvironment env)
	{
		return env.manager;
	}

	public void openResultView()
	{

		PlotGenerator.openNewResultWindow(manager.getDataCollector());
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		// TODO Auto-generated method stub

	}

}
