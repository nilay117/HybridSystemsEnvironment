package edu.ucsc.cross.hse.models.network.syncconsensus;

import edu.cross.ucsc.hse.core.chart.Chart;
import edu.cross.ucsc.hse.core.chart.LabelType;
import edu.ucsc.cross.hse.core.environment.Environment;
import edu.ucsc.cross.hse.core.task.TaskManager;
import java.awt.Font;

/*
 * This is an example of what an empty task execution class looks like. You can copy this code to create your own
 * version.
 */
public class TaskExecutor extends TaskManager
{

	// Everything within the taskQueue() method will be executed
	@Override
	public void taskQueue()
	{
		// Your tasks go here
		runSyncNetwork();
		// SampleAndHoldTasks.signalGeneratorSampleAndHoldSimulation(20.0, .3, .1);
	}

	public void runSyncNetwork()
	{
		Network net = Network.generateRandomNetwork(25, .4, .1);
		Environment env = new Environment();
		env.add(net);
		env.add(xyCombination());
		env.start(20.0, 1000000);
		xyCombination().plot(env);
	}

	public static Chart xyCombination()
	{
		// Create a new plot configuration with specified width (600.0) and height (600.0)
		Chart plot = new Chart(1000.0, 800.0);
		// plot.
		// Set layout to generate two horizontal plots with plot 0 on top and plot 1 on the bottom
		plot.setLayout(new Integer[][]
		{
				{ 1, 1, 1, 2, 2 },
				{ 0, 0, 0, 2, 2 } });

		// Select data to display
		// * selections should be a string that matches the variable name of the data to be selected
		// * null is used to select time as the x axis values
		plot.sub(0).setAxisSelections(null, "stateValue");
		plot.sub(1).setAxisSelections(null, "controllerValue");
		plot.sub(2).setAxisSelections(null, "eventTimer");

		// Select axis labels
		// * null is used to remove an axis label completely
		// Chart.EMPTY is used to remove an axis label but keep the space so the sub chart stays alligned with grid
		plot.sub(0).setAxisLabels("Time (sec)", "X Value");
		plot.sub(1).setAxisLabels("Time (sec)", "Y Value");
		plot.sub(2).setAxisLabels(null, null);

		// Specify legend visibility
		plot.sub(0).setDisplayLegend(false);
		plot.sub(1).setDisplayLegend(false);
		plot.sub(2).setDisplayLegend(false);

		// Specify overall title for the plot
		// * null is used to indicate no sub plot title
		// * there are no sub plot titles by default so following lines can be ommitted for no sub plot titles
		plot.sub(0).setTitle(null);
		plot.sub(1).setTitle(null);
		plot.sub(2).setTitle("X Value vs Y Value");

		// Specify overall title for the plot
		// * null is used to indicate no main title
		// * there is no main title by default so following line can be ommitted if no main title is desired
		plot.addMainTitle("Signal Generator", null);

		plot.editFonts(LabelType.TITLE).set(new Font("Tahoma", Font.ITALIC, 18));
		plot.editFonts(LabelType.RANGE_AXIS_LABEL).set(new Font("Tahoma", Font.BOLD, 14));
		plot.editFonts(LabelType.DOMAIN_AXIS_LABEL).set(new Font("Tahoma", Font.BOLD, 14));
		plot.editFonts(LabelType.RANGE_AXIS_TICK_LABEL).set(new Font("Tahoma", Font.PLAIN, 12));
		plot.editFonts(LabelType.DOMAIN_AXIS_TICK_LABEL).set(new Font("Tahoma", Font.PLAIN, 12));
		plot.editFonts(LabelType.LEGEND_ITEM).set(new Font("Tahoma", Font.PLAIN, 10));
		return plot;
	}

	public static void main(String args[])
	{
		launch();
	}

}
