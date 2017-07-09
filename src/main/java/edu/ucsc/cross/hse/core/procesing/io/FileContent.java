package edu.ucsc.cross.hse.core.procesing.io;

import java.util.ArrayList;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.framework.environment.HybridEnvironment;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import javafx.scene.chart.PieChart.Data;

public enum FileContent
{
	DATA(
		Data.class.getSimpleName() + "@",
		State.class.getSimpleName() + "@"),
	SETTINGS(
		SettingConfigurer.class.getSimpleName()),
	COMPONENT(
		Component.class.getSimpleName(),
		HybridEnvironment.class.getSimpleName()),
	UNKNOWN();

	private ArrayList<String> identifiers;;

	public static FileContent[] getContentArray(boolean load_data, boolean load_settings)
	{
		ArrayList<FileContent> contentz = new ArrayList<FileContent>();
		contentz.add(COMPONENT);
		if (load_data)
		{
			contentz.add(DATA);
		}
		if (load_settings)
		{
			contentz.add(SETTINGS);
		}
		return contentz.toArray(new FileContent[contentz.size()]);
	}

	private FileContent(String... identifiers)
	{
		this.identifiers = new ArrayList<String>();
		for (String identifier : identifiers)
		{
			this.identifiers.add(identifier);
		}
	}

	public static FileContent getFileContentType(String file_name)
	{
		FileContent element = FileContent.UNKNOWN;
		for (FileContent attempt : FileContent.values())
		{
			for (String id : attempt.identifiers)
			{
				if (file_name.contains(id))
				{
					element = attempt;
					break;
				}
			}
		}
		return element;
	}
}
