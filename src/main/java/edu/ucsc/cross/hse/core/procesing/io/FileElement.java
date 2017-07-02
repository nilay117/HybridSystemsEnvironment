package edu.ucsc.cross.hse.core.procesing.io;

import java.util.ArrayList;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import javafx.scene.chart.PieChart.Data;

public enum FileElement
{
	DATA(
		Data.class.getSimpleName(),
		State.class.getSimpleName()),
	ENVIRONMENT(
		EnvironmentContent.class.getSimpleName()),
	SETTINGS(
		SettingConfigurer.class.getSimpleName()),
	COMPONENT(
		Component.class.getSimpleName()),
	UNKNOWN();

	private ArrayList<String> identifiers;

	private FileElement(String... identifiers)
	{
		this.identifiers = new ArrayList<String>();
		for (String identifier : identifiers)
		{
			this.identifiers.add(identifier);
		}
	}

	public static FileElement getFileElementType(String file_name)
	{
		FileElement element = FileElement.UNKNOWN;
		for (FileElement attempt : FileElement.values())
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
