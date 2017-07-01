package edu.ucsc.cross.hse.core.procesing.io;

public enum FileComponent
{
	COMPONENT(
		"Component"),
	DATA(
		"Data"),
	SETTINGS(
		"Settings"),
	CONFIGURATION(
		"Configuration");

	public final String label;

	private FileComponent(String label)
	{
		this.label = label;
	}
}
