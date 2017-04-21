package edu.ucsc.cross.hybrid.env.core.settings;

import bs.commons.objects.manipulation.XMLParser;

public class PrintOutSettings
{

	public final boolean printProgressUpdates; //= true;
	public Integer totalSimTimePrintOuts; //  The total number of times that the current simulation time will be printed to the console, set to null for no notifications
	public final boolean printDiscreteEventIndicator; //= true;
	public boolean printStoreDataIndicator;
	public boolean printStoreDataReport;

	public PrintOutSettings()
	{
		totalSimTimePrintOuts = 20;
		printStoreDataIndicator = true;
		printProgressUpdates = true;
		printDiscreteEventIndicator = true;
		printStoreDataReport = true;
	}

	public static void main(String args[])
	{
		//FileSystemOperator.createOutputFile("resources/io/settings.xml", XMLParser.serializeObject(new IOSettings()));
		PrintOutSettings inputted = (PrintOutSettings) XMLParser.getObject("resources/io/settings.xml");
		System.out.println(inputted.printProgressUpdates);
	}
}
