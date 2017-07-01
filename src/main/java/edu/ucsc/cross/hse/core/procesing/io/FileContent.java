package edu.ucsc.cross.hse.core.procesing.io;

public enum FileContent
{
	ENVIRONMENT_CONFIGURATION(
		"configuration"),
	COMPONENT_CONFIGURATIONS(
		"components"),
	ALL_DATA(
		"allData"),
	COMPONENT_DATA(
		"componentData"),
	SETTINGS(
		"settings");

	public final String sub_directory;

	private FileContent(String sub_directory)
	{
		this.sub_directory = sub_directory;
	}
}
