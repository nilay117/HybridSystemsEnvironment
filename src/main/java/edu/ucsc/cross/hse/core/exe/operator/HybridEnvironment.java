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

/*
 * The main class of the hybrid systeme environment, conting the manager, contents, and the setting configurer.
 */
// @Loggable(Loggable.DEBUG)
public class HybridEnvironment
{

	private EnvironmentManager unRunCopy;

	public EnvironmentManager getManager()
	{
		return manager;
	}

	public EnvironmentManager manager;

	public HybridEnvironment()
	{
		manager = new EnvironmentManager();
	}

	public SettingInterface settings()
	{
		return manager.getSettings();
	}

	public EnvironmentContent contents()
	{
		return manager.getContents();
	}

	public void loadNewEnv(HybridEnvironment env)
	{
		manager.setSettings(env.getManager().getSettings());
		manager.setContents(env.getManager().getContents());
		// manager.createNewEnvironment(false);
	}

	public void save(String file_path)
	{
		manager.save(file_path, this);
	}

	public void start()
	{
		start(false);
	}

	public void start(Double run_time, boolean plot)
	{

		manager.start();
		if (plot)
		{
			PlotGenerator.openNewResultWindow(manager.getDataCollector());
		}
	}

	public void start(boolean plot)
	{
		manager.start();
		if (plot)
		{
			PlotGenerator.openNewResultWindow(manager.getDataCollector());
		}
	}

	public void start(ExecutionParameters params)
	{
		manager.getSettings().setExecutionParameters(params);
		// manager.getContentManager().loadWorld();
		manager.getExecutionMonitor().launchEnvironment();
	}

	public void addContent(HybridSystem<?>... systems)
	{
		for (HybridSystem<?> system : systems)
		{
			manager.getContents().addSystems(system);
		}
	}

	public static HybridEnvironment load(File file)
	{
		HybridEnvironment env = EnvironmentManager.load(file);
		return env;
	}

}
