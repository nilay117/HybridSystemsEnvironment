package datagenerator;

import edu.cross.ucsc.hse.core.chart.ChartConfiguration;
import edu.ucsc.cross.hse.core.environment.Environment;
import edu.ucsc.cross.hse.core.task.TaskManager;
import java.util.ArrayList;

public class DataGeneratorOperations extends TaskManager
{

	@Override
	public void taskQueue()
	{
		exampleOne();

	}

	public static void exampleOne()
	{
		Environment e = new Environment();
		e.getSettings().getLogSettings().printInfo = false;
		// DataGeneratorSystem getGenerator = getGenerator(1.0, 1.0, 0.0, 0.0);
		// SignalGenertor sig = new SignalGenertor(.5, 1.0);
		// ValuedSampleAndHoldSystem sys = new ValuedSampleAndHoldSystem(.1, sig);
		// ValuedSampleAndHoldSystem sys1 = new ValuedSampleAndHoldSystem(.05, sig);
		// ValuedSampleAndHoldSystem sys2 = new ValuedSampleAndHoldSystem(.01, sig);
		// e.add(sig);
		// e.add(sys);
		// e.add(sys1);
		// e.add(sys2);
		// e.
		// e.add(new SignalGenertor(.5, 1.0));
		e.start(4.0, 200000);
		// dataGeneratorStateHybridChart2().plot(e);
		// sampleAndHold().plot(e);
		// dataGeneratedHybridChart().plot(e);
	}

	public static Environment getConfiguredEnvironment()
	{
		Environment e = new Environment();
		e.getSettings().getLogSettings().printInfo = true;
		e.getSettings().getLogSettings().printDebug = true;
		e.getSettings().getLogSettings().printError = true;
		e.getSettings().getOutputSettings().appendFilesWithNumericDate = false;
		e.getSettings().getOutputSettings().saveChartsToFile = true;
		e.getSettings().getOutputSettings().saveEnvironmentToFile = true;
		e.getSettings().getOutputSettings().saveConfigurationToFile = true;
		// e.getSettings().getOutputSettings().HybridChartFileFormat;
		return e;
	}

	public static SensorSystem getGenerator(Double generated_data_size, Double generation_interval,
	Double initial_data_size, Double initial_generation_time)
	{

		// initialize parameters with argument values
		SensorParameters parameters = new SensorParameters(generated_data_size, generation_interval);

		// initialize state with argument values
		SensorState state = new SensorState(initial_data_size, initial_generation_time);

		// create data generator hybrid system
		SensorSystem system = new SensorSystem(state, parameters);

		return system;

	}

	public static ArrayList<SensorSystem> getRandomizedGenerators(Integer num_generators, Double random_interval,
	Double fixed_interval, Double random_gen_size, Double fixed_gen_size)
	{
		ArrayList<SensorSystem> systems = new ArrayList<SensorSystem>();

		for (int index = 0; index < num_generators; index++)
		{
			// generate random interval and size
			Double randomInterval = random_interval * Math.random() + fixed_interval;

			// generate random size
			Double randomSize = random_gen_size * Math.random() + fixed_gen_size;

			// initialize parameters
			SensorParameters parameters = new SensorParameters(randomInterval, randomSize);

			// initialize state
			SensorState state = new SensorState(0, parameters.generationInterval);

			// initialize system
			SensorSystem system = new SensorSystem(state, parameters);

			// load the current data generator system into environment
			systems.add(system);

		}

		return systems;

	}

	public static ChartConfiguration dataGeneratedHybridChart()
	{
		// Create a new plot configuration with specified width (600.0) and height (600.0)
		ChartConfiguration plot = new ChartConfiguration(600.0, 600.0);

		// Select data to display
		plot.chartProperties(0).setAxisSelections(null, "dataGenerated");

		// Select axis label
		plot.chartProperties(0).setAxisLabels("Time (sec)", "Data Generated (Mb)");

		// Specify legend visibility
		plot.chartProperties(0).setDisplayLegend(false);

		return plot;
	}

	public static ChartConfiguration timeToNextGenerationHybridChart()
	{
		// Create a new plot configuration with specified width (600.0) and height (600.0)
		ChartConfiguration plot = new ChartConfiguration(600.0, 600.0);

		// Select data to display
		plot.chartProperties(0).setAxisSelections(null, "dataGenerated");

		// Select axis label
		plot.chartProperties(0).setAxisLabels("Time (sec)", "Data Generated (Mb)");

		// Specify legend visibility
		plot.chartProperties(0).setDisplayLegend(false);

		return plot;
	}

	public static ChartConfiguration dataGeneratorStateHybridChart()
	{
		// Create a new plot configuration with specified width (600.0) and height (600.0)
		ChartConfiguration plot = new ChartConfiguration(600.0, 600.0);

		// Set layout to generate two horizontal plots with plot 0 on top and plot 1 on the bottom
		plot.setLayout(new Integer[][]
		{
				{ 0, 0 },
				{ 1, 1 } });

		// Select data to display
		plot.chartProperties(0).setAxisSelections(null, "dataGenerated");
		plot.chartProperties(0).setAxisSelections(null, "timeToNextData");

		// Select axis labels
		plot.chartProperties(0).setAxisLabels("Time (sec)", "Data Generated (Mb)");
		plot.chartProperties(1).setAxisLabels("Time (sec)", "Time to Next Generation (sec)");

		// Specify legend visibility
		plot.chartProperties(0).setDisplayLegend(false);
		plot.chartProperties(1).setDisplayLegend(false);

		return plot;
	}

	public static ChartConfiguration dataGeneratorStateHybridChart2()
	{
		// Create a new plot configuration with specified width (600.0) and height (600.0)
		ChartConfiguration plot = new ChartConfiguration(1200.0, 600.0);
		// plot.
		// Set layout to generate two horizontal plots with plot 0 on top and plot 1 on the bottom
		plot.setLayout(new Integer[][]
		{
				{ 1, 0 },
				{ 1, 0 } });

		// Select data to display
		plot.chartProperties(0).setAxisSelections(null, "data");
		plot.chartProperties(1).setAxisSelections(null, "timer");

		// Select axis labels
		plot.chartProperties(0).setAxisLabels("Time (sec)", "Data Generated (Mb)");
		plot.chartProperties(1).setAxisLabels("Time (sec)", "Time to Next Generation (sec)");

		// Specify legend visibility
		plot.chartProperties(0).setDisplayLegend(false);
		plot.chartProperties(1).setDisplayLegend(false);

		// Specify overall title for the plot
		plot.addMainTitle("Signal Generator", null);

		return plot;
	}

	public static ChartConfiguration signalGenerator()
	{
		// Create a new plot configuration with specified width (600.0) and height (600.0)
		ChartConfiguration plot = new ChartConfiguration(1200.0, 600.0);
		// plot.
		// Set layout to generate two horizontal plots with plot 0 on top and plot 1 on the bottom
		plot.setLayout(new Integer[][]
		{
				{ 0, 1, 2 },
				{ 0, 1, 2 } });

		// Select data to display
		plot.chartProperties(0).setAxisSelections(null, "xValue");
		plot.chartProperties(1).setAxisSelections(null, "yValue");
		plot.chartProperties(2).setAxisSelections("xValue", "yValue");
		// Select axis labels
		plot.chartProperties(0).setAxisLabels("Time (sec)", "X Value");
		plot.chartProperties(1).setAxisLabels("Time (sec)", "Y Value");
		plot.chartProperties(2).setAxisLabels("X Value", "Y Value");
		// Specify legend visibility
		plot.chartProperties(0).setDisplayLegend(false);
		plot.chartProperties(1).setDisplayLegend(false);
		plot.chartProperties(2).setDisplayLegend(false);
		// Specify overall title for the plot
		plot.addMainTitle("Signal Generator", null);

		return plot;
	}

	public static ChartConfiguration signalGenerator2()
	{
		// Create a new plot configuration with specified width (600.0) and height (600.0)
		ChartConfiguration plot = new ChartConfiguration(1000.0, 800.0);
		// plot.
		// Set layout to generate two horizontal plots with plot 0 on top and plot 1 on the bottom
		plot.setLayout(new Integer[][]
		{
				{ 0, 0, 0, 2, 2 },
				{ 1, 1, 1, 2, 2 } });

		// Select data to display
		plot.chartProperties(0).setAxisSelections(null, "xValue");
		plot.chartProperties(1).setAxisSelections(null, "yValue");
		plot.chartProperties(2).setAxisSelections("xValue", "yValue");
		plot.chartProperties(3).setAxisSelections("yValue", "xValue");
		plot.chartProperties(4).setAxisSelections(null, "translate");
		// Select axis labels
		plot.chartProperties(0).setAxisLabels("Time (sec)", "X Value");
		plot.chartProperties(1).setAxisLabels("Time (sec)", "Y Value");
		plot.chartProperties(2).setAxisLabels("X Value", "Y Value");
		plot.chartProperties(3).setAxisLabels("Y Value", "X Value");
		plot.chartProperties(4).setAxisLabels("Time (sec)", "Translation");
		// Specify legend visibility
		plot.chartProperties(0).setDisplayLegend(false);
		plot.chartProperties(1).setDisplayLegend(false);
		plot.chartProperties(2).setDisplayLegend(false);
		plot.chartProperties(3).setDisplayLegend(false);
		plot.chartProperties(4).setDisplayLegend(false);

		// Specify overall title for the plot
		plot.addMainTitle("Signal Generator", null);

		return plot;
	}

	public static ChartConfiguration sampleAndHold()
	{
		// Create a new plot configuration with specified width (600.0) and height (600.0)
		ChartConfiguration plot = new ChartConfiguration(1200.0, 600.0);
		// plot.
		// Set layout to generate two horizontal plots with plot 0 on top and plot 1 on the bottom
		plot.setLayout(new Integer[][]
		{
				{ 0, 0 },
				{ 0, 0 } });

		// Select data to display
		plot.chartProperties(0).setAxisSelections("timer", "data");

		// Select axis labels
		plot.chartProperties(0).setAxisLabels("Time (sec)", "Data Generated (Mb)");

		// Specify legend visibility
		plot.chartProperties(0).setDisplayLegend(false);

		// Specify overall title for the plot
		plot.addMainTitle("Signal Generator", null);

		return plot;
	}

	public static SensorSystem getRandomizedGenerator(Double random_interval, Double fixed_interval,
	Double random_gen_size, Double fixed_gen_size)
	{

		SensorParameters p = new SensorParameters(random_interval * Math.random() + fixed_interval,
		random_gen_size * Math.random() + fixed_gen_size);

		SensorState state = new SensorState(0, p.generationInterval);

		SensorSystem sys = new SensorSystem(state, p);
		return sys;
	}

	public static void main(String args[])
	{
		launch();
	}

}
