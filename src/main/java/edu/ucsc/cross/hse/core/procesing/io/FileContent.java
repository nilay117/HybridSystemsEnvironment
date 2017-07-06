package edu.ucsc.cross.hse.core.procesing.io;

import java.util.ArrayList;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import javafx.scene.chart.PieChart.Data;

public enum FileContent
{
	DATA(
		Data.class.getSimpleName() + "@",
		State.class.getSimpleName() + "@"),
	ENVIRONMENT(
		EnvironmentContent.class.getSimpleName()),
	SETTINGS(
		SettingConfigurer.class.getSimpleName()),
	COMPONENT(
		Component.class.getSimpleName()),
	UNKNOWN();

	private ArrayList<String> identifiers;;

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
