package datagenerator;

import circlegenerator.CircleSystem;
import com.be3short.io.format.ImageFormat;
import com.be3short.obj.modification.XMLParser;
import edu.cross.ucsc.hse.core.chart.ChartConfiguration;
import edu.ucsc.cross.hse.core.environment.Environment;
import edu.ucsc.cross.hse.core.setting.ComputationSettings.IntegratorType;
import edu.ucsc.cross.hse.core.task.TaskManager;
import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DataGeneratorTasks extends TaskManager
{

	/*
	 * Queue method that executes everything listed within when application starts*
	 */
	@Override
	public void taskQueue()
	{
		// openEnvironmentAndPlot();//
		// dataGeneratorSimulation();
		// openEnvironmentAndPlot();//
		// dataGeneratorSimulation();//
		long l = (long) 1.9;
		untaryDataGeneratorSimulation();// untaryDataGeneratorSimulation();
		// System.out.println(System.nanoTime() + " " + 10000000E-9);
	}

	/*
	 * Main class needed to run java application
	 */
	public static void main(String args[])
	{
		launch();
	}

	public void openEnvironmentAndPlot()
	{
		File envFile = new FileChooser().showOpenDialog(new Stage());
		Environment env = Environment.createEnvironment(envFile);
		// File datFile = new FileChooser().showOpenDialog(new Stage());
		// EnvironmentData dat = DataMonitor.getCSVData(datFile);
		// env.getData().load(dat.getStoreTimes(), dat.getGlobalStateData());
		// env.getOutputs().generateOutputs(env, true);
		// env.add(xyCombination());
		// System.out.println(XMLParser.serializeObject(env));
		// statesAndTimerChart().plot(env);
		// env.generateOutputs();
		ChartConfiguration HybridChart2 = xOnly();
		ChartConfiguration HybridChart1 = xyCombination();
		HybridChart1.createChart(env);
		HybridChart2.createChart(env);
		// env.add(HybridChart2);// .createChart(env);
		// env.generateOutputs();
	}

	public void exampleSetup()
	{
		Environment environment = new Environment();
		SensorSystem sensor = getSensor(1.0, 1.0, 0.0, 0.0);
		ChartConfiguration plot = sensorPlot();
		environment.add(sensor);
		environment.add(plot);
		environment.start(20.0, 10);
		plot.createChart(environment);
	}

	public static void dataGeneratorSimulation()
	{
		Environment env = getConfiguredEnvironment();

		ChartConfiguration HybridChart1 = xyCombination();
		ChartConfiguration HybridChart2 = xOnly();
		ChartConfiguration HybridChart3 = sensorPlot();
		// env.add(HybridChart1, HybridChart2, HybridChart3);
		for (int i = 0; i < 5; i++)
		{
			CircleSystem signalGenerator = new CircleSystem(Math.random() * 5.0 + .2, Math.random() * 5.0 + .3);
			env.add(signalGenerator);
			env.add(DataGeneratorOperations.getRandomizedGenerator(1.0, 1.0, 1.0, 1.0));
		}
		env.start(10.0, 100000);
		// xyCombination().createChart(env);
		// HybridChart1.createChart(env);
		// HybridChart2.createChart(env);
		// HybridChart3.createChart(env);
		// env.getData().exportToCSVFile();// new File("output/testCSV.csv"));
		// env.save(new File("output/test"), false);
		// //env.save(new File("output/testDat"), true);
		// ChartView cv = new ChartView(env.getData(), HybridChart1, new Stage());
		// cv.setChartProperties(HybridChart2);

	}

	public static SensorSystem getSensor(Double generated_data_size, Double generation_interval,
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

	public static void untaryDataGeneratorSimulation()
	{
		Environment env = getConfiguredEnvironment();

		ChartConfiguration HybridChart1 = xyCombination();
		ChartConfiguration HybridChart2 = xOnly();
		ChartConfiguration HybridChart3 = sensorPlot();
		// env.add(HybridChart1, HybridChart2, HybridChart3);
		for (int i = 0; i < 1; i++)
		{
			// CircleSystem signalGenerator = new CircleSystem(Math.random() * 5.0 + .2, Math.random() * 5.0 + .3);
			// åenv.add(signalGenerator);
			env.add(DataGeneratorOperations.getRandomizedGenerator(1.0, 1.0, 1.0, 1.0));
		}

		SensorSystem test = DataGeneratorOperations.getRandomizedGenerator(1.0, 1.0, 1.0, 1.0);
		env.add(test);
		env.start(10.0, 100000);
		sensorPlot().createChart(env);
		// xyCombination().createChart(env);
		// HybridChart1.createChart(env);
		// HybridChart2.createChart(env);
		// HybridChart3.createChart(env);
		// env.getData().exportToCSVFile();// new File("output/testCSV.csv"));

		// System.out.println(XMLParser.serializeObject(env.getData().getSolution(test.getState())));
		// env.getData().getSolution(test.getState()).exportToCSVFile(new File("output/testCSV.csv"));
		// env.save(new File("output/test"), false);
		// //env.save(new File("output/testDat"), true);
		// ChartView cv = new ChartView(env.getData(), HybridChart1, new Stage());
		// cv.setChartProperties(HybridChart2);

	}

	public static void zuntaryDataGeneratorSimulation()
	{
		Environment env = getConfiguredEnvironment();

		ChartConfiguration HybridChart1 = xyCombination();
		ChartConfiguration HybridChart2 = xOnly();
		ChartConfiguration HybridChart3 = sensorPlot();
		// env.add(HybridChart1, HybridChart2, HybridChart3);
		for (int i = 0; i < 1; i++)
		{
			// CircleSystem signalGenerator = new CircleSystem(Math.random() * 5.0 + .2, Math.random() * 5.0 + .3);
			// åenv.add(signalGenerator);
			env.add(DataGeneratorOperations.getRandomizedGenerator(1.0, 1.0, 1.0, 1.0));
		}

		SensorSystem test = DataGeneratorOperations.getRandomizedGenerator(1.0, 1.0, 1.0, 1.0);
		env.add(test);
		env.start(10.0, 100000);
		sensorPlot().createChart(env);
		// xyCombination().createChart(env);
		// HybridChart1.createChart(env);
		// HybridChart2.createChart(env);
		// HybridChart3.createChart(env);
		env.getData().exportToCSVFile();// new File("output/testCSV.csv"));

		System.out.println(XMLParser.serializeObject(env.getData().getSolution(test.getState())));
		env.getData().getSolution(test.getState()).exportToCSVFile(new File("output/testCSV.csv"));
		// env.save(new File("output/test"), false);
		// //env.save(new File("output/testDat"), true);
		// ChartView cv = new ChartView(env.getData(), HybridChart1, new Stage());
		// cv.setChartProperties(HybridChart2);

	}

	public static ChartConfiguration xyCombination()
	{
		// Create a new plot configuration with specified width (600.0) and height (600.0)
		ChartConfiguration plot = new ChartConfiguration(1000.0, 800.0);
		// plot.
		// Set layout to generate two horizontal plots with plot 0 on top and plot 1 on the bottom
		plot.setLayout(new Integer[][]
		{
				{ 1, 1, 1, 2, 2 },
				{ 0, 0, 0, 2, 2 } });

		// Select data to display
		// * selections should be a string that matches the variable name of the data to be selected
		// * null is used to select time as the x axis values
		plot.chartProperties(0).setAxisSelections(null, "xValue");
		plot.chartProperties(1).setAxisSelections(null, "yValue");
		plot.chartProperties(2).setAxisSelections("xValue", "yValue");

		// Select axis labels
		// * null is used to remove an axis label completely
		// HybridChart.EMPTY is used to remove an axis label but keep the space so the sub HybridChart stays alligned
		// with grid
		plot.chartProperties(0).setAxisLabels("Time (sec)", "X Value");
		plot.chartProperties(1).setAxisLabels("Time (sec)", "Y Value");

		// Specify legend visibility
		plot.chartProperties(0).setDisplayLegend(true);
		plot.chartProperties(1).setDisplayLegend(true);
		plot.chartProperties(2).setDisplayLegend(true);

		// Specify overall title for the plot
		// * null is used to indicate no sub plot title
		// * there are no sub plot titles by default so following lines can be ommitted for no sub plot titles
		plot.chartProperties(0).setTitle(null);
		plot.chartProperties(1).setTitle(null);
		plot.chartProperties(2).setTitle("X Value vs Y Value");

		// Specify overall title for the plot
		// * null is used to indicate no main title
		// * there is no main title by default so following line can be ommitted if no main title is desired
		plot.addMainTitle("Signal Generator", null);

		return plot;
	}

	public static ChartConfiguration xOnly()
	{
		// Create a new plot configuration with specified width (600.0) and height (600.0)
		ChartConfiguration plot = new ChartConfiguration(900.0, 600.0);

		// Select data to display
		// * selections should be a string that matches the variable name of the data to be selected
		// * null is used to select time as the x axis values
		plot.chartProperties(0).setAxisSelections(null, "xValue");

		// Select axis labels
		// * null is used to hide an axis label
		plot.chartProperties(0).setAxisLabels("Time (sec)", "X Value");

		// Specify legend visibility
		plot.chartProperties(0).setDisplayLegend(true);

		// Specify overall title for the plot
		// * null is used to indicate no sub plot title
		// * there are no sub plot titles by default so following lines can be ommitted for no sub plot titles
		plot.chartProperties(0).setTitle(null);

		// Specify overall title for the plot
		// * null is used to indicate no main title
		// * there is no main title by default so following line can be ommitted if no main title is desired
		plot.addMainTitle(null, null);

		return plot;
	}

	public static ChartConfiguration sensorPlot()
	{
		// Create a new plot configuration with specified width (600.0) and height (600.0)
		ChartConfiguration plot = new ChartConfiguration(650.0, 400.0);
		// plot.
		// Set layout to generate two horizontal plots with plot 0 on top and plot 1 on the bottom
		plot.setLayout(new Integer[][]
		{
				{ 0, },
				{ 1 } });
		// plot.assignColors("DataGeneratorState", Color.black, Color.gray, Color.DARK_GRAY);
		// plot.assignStrokes("DataGeneratorState", new BasicStroke(0.5f));// Select data to display
		// * selections should be a string that matches the variable name of the data to be selected
		// * null is used to select time as the x axis values
		plot.chartProperties(1).setAxisSelections(null, "dataGenerated");
		plot.chartProperties(0).setAxisSelections(null, "timeToNextData");

		// Select axis labels
		// * null is used to hide an axis label
		plot.chartProperties(1).setAxisLabels("Time (sec)", "Data Generated");
		plot.chartProperties(0).setAxisLabels("Time (sec)", "Time to Next Generation");
		// Specify legend visibility
		plot.chartProperties(0).setDisplayLegend(false);
		plot.chartProperties(1).setDisplayLegend(true);
		// Specify overall title for the plot
		// * null is used to indicate no sub plot title
		// * there are no sub plot titles by default so following lines can be ommitted for no sub plot titles
		plot.chartProperties(0).setTitle("Total Sensor Data");
		plot.chartProperties(1).setTitle(null);

		// Specify overall title for the plot
		// * null is used to indicate no main title
		// * there is no main title by default so following line can be ommitted if no main title is desired
		plot.addMainTitle(null, null);
		return plot;
	}

	public static ChartConfiguration sensorPlotCommented()
	{
		// Create a new plot configuration with specified width (600.0) and height (600.0)
		ChartConfiguration plot = new ChartConfiguration(650.0, 400.0);
		// plot.
		// Set layout to generate two horizontal plots with plot 0 on top and plot 1 on the bottom
		plot.setLayout(new Integer[][]
		{
				{ 0, },
				{ 1 } });
		// plot.assignColors("DataGeneratorState", Color.black, Color.gray, Color.DARK_GRAY);
		// plot.assignStrokes("DataGeneratorState", new BasicStroke(0.5f));// Select data to display
		// * selections should be a string that matches the variable name of the data to be selected
		// * null is used to select time as the x axis values
		plot.chartProperties(1).setAxisSelections(null, "dataGenerated");
		plot.chartProperties(0).setAxisSelections(null, "timeToNextData");

		// Select axis labels
		// * null is used to hide an axis label
		plot.chartProperties(1).setAxisLabels("Time (sec)", "Data Generated");
		plot.chartProperties(0).setAxisLabels("Time (sec)", "Time to Next Generation");
		// Specify legend visibility
		// plot.chartProperties(0).setDisplayLegend(true);
		plot.chartProperties(1).setDisplayLegend(true);
		// Specify overall title for the plot
		// * null is used to indicate no sub plot title
		// * there are no sub plot titles by default so following lines can be ommitted for no sub plot titles
		plot.chartProperties(0).setTitle(null);
		plot.chartProperties(1).setTitle(null);

		// Specify overall title for the plot
		// * null is used to indicate no main title
		// * there is no main title by default so following line can be ommitted if no main title is desired
		plot.addMainTitle(null, null);
		return plot;
	}

	/*
	 * Create an environment that is already configured
	 */
	public static Environment getConfiguredEnvironment()
	{

		Environment env = new Environment(); // Environment with default settings

		///// Execution Parameters

		// Maximum time allowed before execution terminates
		env.getSettings().getExecutionParameters().maximumTime = 5.0;
		// Maximum number of jumps allowed before execution terminates
		env.getSettings().getExecutionParameters().maximumJumps = 200;

		///// Log settings

		// Total progress updates to print, ie 10 prints an update at 10% intervals. -1 disables progress output
		env.getSettings().getLogSettings().numProgressUpdateOutputs = 10;
		// Inverval between status print outs
		env.getSettings().getLogSettings().statusPrintInterval = 10.0;
		// Print debug messages
		env.getSettings().getLogSettings().printDebug = true;
		// Print error messages
		env.getSettings().getLogSettings().printError = true;
		// Print information messages
		env.getSettings().getLogSettings().printInfo = true;
		// Print integrator exception warnings
		env.getSettings().getLogSettings().printIntegratorExceptions = true;
		// Print warning messages
		env.getSettings().getLogSettings().printWarning = true;

		///// Output settings

		// Flag indicating if file names should include the date at the end (for uniqueness)
		env.getSettings().getOutputSettings().appendFilesWithNumericDate = false;
		// Default image file format to be used when exporting charts with no format specified
		env.getSettings().getOutputSettings().chartFileFormat = ImageFormat.EPS;
		// Name of the environment configuration file if it were to be saved
		env.getSettings().getOutputSettings().configurationFileName = "environmentConfig";
		// Time between data point storage
		env.getSettings().getOutputSettings().dataPointInterval = .025;
		// Name of the environment file if it were to be saved
		env.getSettings().getOutputSettings().environmentFileName = "environment";
		// Location where results will be automatically stored if auto storage is enabled
		env.getSettings().getOutputSettings().outputDirectory = "output";
		// Flag indicating if environment configuration should be saved in an output file when not specified by the user
		env.getSettings().getOutputSettings().saveChartsToFile = true;
		// Flag indicating if environment configuration should be saved in an output file when not specified by the user
		env.getSettings().getOutputSettings().saveConfigurationToFile = false;
		// Flag indicating if environment should be saved in an output file when not specified by the user
		env.getSettings().getOutputSettings().saveEnvironmentToFile = true;
		// Flag indicating if log should be saved to file
		env.getSettings().getOutputSettings().saveLogToFile = false;

		///// Computation Settings

		// Factor that event handling convergence value will be reduced when event handling error occurs
		env.getSettings().getComputationSettings().convergenceErrorCorrectionFactor = 1.0;
		// Convergence threshold of an event
		env.getSettings().getComputationSettings().eventHandlerConvergenceThreshold = .00000000000001;
		// Event handler maximum interval to check for an event
		env.getSettings().getComputationSettings().eventHandlerMaximumCheckInterval = .5E-6;
		// Integrator type to be used
		env.getSettings().getComputationSettings().integratorType = IntegratorType.DORMAND_PRINCE_853;
		// Factor that event handling interval value will be reduced when event handling error occurs
		env.getSettings().getComputationSettings().intervalErrorCorrectionFactor = 1.0;
		// Factor that iteration count will be multiplied by when iteration count error occurs
		env.getSettings().getComputationSettings().iterationCountErrorCorrectionFactor = 2;
		// Maximum number of event handler iterations
		env.getSettings().getComputationSettings().maxEventHandlerIterations = 10;
		// Maximum step size for variable step integrator
		env.getSettings().getComputationSettings().odeMaximumStepSize = .001;
		// Ode step size if using a fixed step integrator, or minimum ode step size of a variable step integrator
		env.getSettings().getComputationSettings().odeMinimumStepSize = .5E-5;
		// Relative tolerance of the ode solver
		env.getSettings().getComputationSettings().odeRelativeTolerance = 1.0e-9;
		// Absolute tolerance of the ode solver
		env.getSettings().getComputationSettings().odeSolverAbsoluteTolerance = 1.0e-9;
		// Factor to reduce the maximum /fixed step size when a step size related error occurs
		env.getSettings().getComputationSettings().stepSizeErrorMaxCorrectionFactor = 1.0;
		// Factor to reduce minimum step size (if using variable step integrator) when a step size related error occurs
		env.getSettings().getComputationSettings().stepSizeErrorMinCorrectionFactor = 1.0;

		env.getSettings().getInterfaceSettings().runInRealTime = false;
		return env;
	}
}
