package edu.ucsc.cross.hse.tools.ui.resultview;

import bs.gui.components.menu.OneClickSelectionMenu;
import com.be3short.data.cloning.ObjectCloner;
import com.be3short.data.file.general.FileSystemInteractor;
import com.be3short.data.file.xml.XMLParser;
import com.be3short.jfx.event.menu.ActionDefinition;
import com.be3short.jfx.event.menu.ActionEventHandler;
import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.exe.access.ObjectManipulator;
import edu.ucsc.cross.hse.core.exe.monitor.Console;
import edu.ucsc.cross.hse.core.obj.data.DataSeries;
import edu.ucsc.cross.hse.core.obj.data.DataSet;
import edu.ucsc.cross.hse.core.obj.structure.State;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.ui.HorizontalAlignment;

@Loggable(Loggable.INFO)
public class ChartView extends ActionEventHandler implements DataView
{

	public BorderPane getPane()
	{
		return pane;
	}

	private JFreeChart chart;

	public JFreeChart getChart()
	{
		return chart;
	}

	/**
	 * Creates a chart.
	 *
	 * @param dataset
	 *            a dataset.
	 *
	 * @return A chart.
	 */
	public BorderPane pane;
	public DataSet data;
	public String xLabel;
	private ChartProperties props;
	public ArrayList<String> yFilter;
	public ChartPanel panel;

	public ChartView(DataSet data)
	{
		super(ChartActions.AXIS.rootItems());
		props = new ChartProperties(data);

		xLabel = "Value";
		this.data = data;

		setupMenus();
		pane = new BorderPane();
		yFilter = new ArrayList<String>();
		create(null);
	}

	private JFreeChart createChart(XYDataset dataset)
	{

		chart = ChartFactory.createXYLineChart(null, props.getxAxisLabel(), props.getyAxisLabel(), dataset,
		PlotOrientation.VERTICAL, true, true, true);
		String fontName = "Times";
		// chart.getTitle().setFont(new Font(fontName, Font.PLAIN, 18));
		// chart.addSubtitle(new TextTitle("Source: http://www.ico.org/historical/2010-19/PDF/HIST-PRICES.pdf",
		// new Font(fontName, Font.PLAIN, 14)));

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.getDomainAxis().setAxisLineVisible(false);//
		plot.setDomainGridlinesVisible(true);
		plot.getRangeAxis().setAxisLineVisible(false);//
		plot.setDomainGridlinePaint(Color.gray);
		plot.setRangeGridlinePaint(Color.gray);
		plot.setDomainGridlinesVisible(true);
		if (props.getxFilter().equals(props.simulationTimeVarName))
		{
			plot.getDomainAxis().setRange(0.0, data.getManager().getTimeOperator().getSimulationTime());
		}
		// plot.setDomainCrosshairVisible(true);
		// plot.setRangeCrosshairVisible(true);
		plot.setBackgroundPaint(null);
		// plot.getDomainAxis().setLowerMargin(10.0);
		// plot.getDomainAxis().setLowerMargin(10.0);
		plot.getDomainAxis().setLabelFont(new Font(fontName, Font.PLAIN, 14));
		plot.getDomainAxis().setTickLabelFont(new Font(fontName, Font.PLAIN, 14));
		plot.getRangeAxis().setLabelFont(new Font(fontName, Font.PLAIN, 14));
		plot.getRangeAxis().setTickLabelFont(new Font(fontName, Font.PLAIN, 14));
		plot.getRangeAxis().setLabelLocation(AxisLabelLocation.MIDDLE);
		chart.getLegend().setItemFont(new Font(fontName, Font.PLAIN, 14));
		chart.getLegend().setFrame(BlockBorder.NONE);
		// chart.getLegend().visible = false;
		chart.setBackgroundPaint(null);
		// chart.setPadding(new RectangleInsets(20, 20, 20, 20));
		chart.getLegend().setHorizontalAlignment(HorizontalAlignment.CENTER);
		chart.getLegend().setBackgroundPaint(null);

		HybridXYLineAndShapeRenderer renderer = new HybridXYLineAndShapeRenderer(true, false);// true, false);
		// rend.setPlotDiscontinuous(false);
		// rend.setGapThreshold(0.0001);
		// rend.setPlotImages(true);// .setPlotLines(false);

		renderer.setDrawSeriesLineAsPath(false);
		// rend.setGapThresholdType(UnitType.ABSOLUTE);
		// rend.setAutoPopulateSeriesStroke(false);
		plot.setRenderer(renderer);
		// renderer.setSeriesStroke(0, new BasicStroke(1.0f));
		// renderer.setSeriesStroke(1, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.CAP_BUTT, 1.0f, new
		// float[]
		// { 2.0f, 2.0f }, 0.0f));
		// renderer.setSeriesStroke(2, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.CAP_BUTT, 1.0f, new
		// float[]
		// { 1.5f, 5.0f }, 0.0f));
		// renderer.setSeriesPaint(0, Color.blue);
		//
		// renderer.setSeriesPaint(2, Color.green);
		// renderer.setSeriesPaint(1, Color.red);
		// FileSystemInteractor.createOutputFile(new File("chart/Chart.xml"), XMLParser.serializeObject(chart));
		// renderer.setSeriesStroke(0, new BasicStroke(1.5f));
		// renderer.setSeriesStroke(1,
		// new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]
		// { 6.0f, 6.0f }, 0.0f));
		// renderer.setSeriesStroke(2,
		// new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]
		// { 2.0f, 6.0f }, 0.0f));
		// renderer.setSeriesStroke(3,
		// new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]
		// { 2.0f, 2.0f }, 0.0f));
		// renderer.setSeriesPaint(0, Color.blue);
		//
		// renderer.setSeriesPaint(1, Color.green);
		// renderer.setSeriesPaint(2, Color.red);
		// renderer.setSeriesPaint(3, Color.ORANGE);
		// chart.getLegend().setVisible(false);
		// plot.getSeriesRenderingOrder()
		// XYItemRenderer r = plot.getRenderer();
		// if (r instanceof XYLineAndShapeRenderer)
		// {
		// XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
		// renderer.setDrawSeriesLineAsPath(false);// renderer.setBaseShapesVisible(false);
		// // renderer.setDrawSeriesLineAsPath(true);
		// // set the default stroke for all series
		// renderer.setAutoPopulateSeriesStroke(false);
		//
		// }

		return chart;

	}

	public void drawChartSVG(SVGGraphics2D graphics, Rectangle location)
	{

		chart.draw(graphics, location);
	}

	public ArrayList<XYSeries> createDatasetJ()
	{
		// BouncingBallState.stateSTest();
		ArrayList<XYSeries> series = new ArrayList<XYSeries>();
		Integer i = 0;
		for (ObjectManipulator e : data.getGlobalStateData().keySet())
		{

			if (yFilter.contains(e.getField().getName()))
			{
				DataSeries<Double> d = data.getGlobalStateData().get(e);
				XYSeries s1 = new XYSeries(e.getField().getName());
				Double prevTime = -1.0;
				Double prevVal = null;
				for (Double v : d.getAllStoredData())
				{
					if (data.getStoreTimes().get(i).getTime() == prevTime)
					{
						XYDataItem dat = new XYDataItem(data.getStoreTimes().get(i - 1).getTime(), v.doubleValue());
						XYDataItem dat2 = new XYDataItem(data.getStoreTimes().get(i - 1).getTime(),
						prevVal.doubleValue());
						s1.add(dat);
						s1.add(dat2);
					}
					prevTime = data.getStoreTimes().get(i++).getTime();
					prevVal = v.doubleValue();
					// s1.add(data.getStoreTimes().get(i++).getTime(), v.doubleValue());
				}
				i = 0;
				series.add(s1);
			}
		}
		return series;
	}

	public XYDataset createDataset()
	{
		ArrayList<String> names = new ArrayList<String>();
		// BouncingBallState.stateSTest();
		XYSeriesCollection dataset = new XYSeriesCollection();
		Integer i = 0;
		StandardXYItemRenderer rend = new StandardXYItemRenderer();
		rend.setPlotDiscontinuous(false);
		HashMap<String, XYSeries> ser = new HashMap<String, XYSeries>();
		for (ObjectManipulator e : data.getGlobalStateData().keySet())
		{

			if (props.fufilsFilters(e))
			{
				// (e.getField().getName());
				String label = getLegendLabel(e, names);
				names.add(label);
				DataSeries<Double> d = data.getGlobalStateData().get(e);
				XYSeries s1 = new XYSeries(label);
				DataSeries<Double> xS = null;
				if (!props.getxFilter().equals(props.simulationTimeVarName))
				{
					xS = data.getGlobalStateData().get(props.getXCounter(e));
				}

				Integer prevTime = -1;

				Double prevVal = null;
				for (Double v : d.getAllStoredData())
				{
					// if ((data.getStoreTimes().get(i).getJumps() != prevTime))
					// {
					// if (!v.equals(prevVal))
					// {
					// XYDataItem dat = new XYDataItem(data.getStoreTimes().get(i).getTime(), v.doubleValue());
					// s1.add(dat);
					// }
					// } else
					Double x = data.getStoreTimes().get(i).getTime();
					if (xS != null)
					{
						x = xS.getAllStoredData().get(i);
					}

					XYDataItem dat = new XYDataItem(x.doubleValue(), v.doubleValue());
					s1.add(dat);

					prevVal = v;
					prevTime = data.getStoreTimes().get(i++).getJumps();

					// s1.add(data.getStoreTimes().get(i++).getTime(), v.doubleValue());
				}
				i = 0;
				ser.put(s1.getKey().toString(), s1);

			}

		}
		ArrayList<String> serName = new ArrayList<String>(ser.keySet());
		Collections.sort(serName);
		for (String se : serName)
		{
			dataset.addSeries(ser.get(se));
		}
		// for (XYSeries s : createDatasetJ())
		// {
		// dataset.addSeries(s);
		// }
		return dataset;
	}

	public String getLegendLabel(ObjectManipulator data, ArrayList<String> names)
	{
		String label = data.getParent().getClass().getSimpleName();
		try
		{
			State state = ((State) data.getParent());
			label = state.getName();
		} catch (Exception notState)
		{

		}
		String labelBase = label;
		int append = 0;
		while (names.contains(label))
		{
			append++;
			label = labelBase + "_" + append;
		}

		return label;
	}

	public ArrayList<String> getFieldNames()
	{
		ArrayList<String> fieldNames = new ArrayList<String>();
		for (ObjectManipulator e : data.getGlobalStateData().keySet())
		{

			if (!fieldNames.contains(e.getField().getName()))
			{
				fieldNames.add(e.getField().getName());
			}
		}
		return fieldNames;
	}

	public void create(String y_filter)
	{

		create();
	}

	public void toggleLegendVisibility(boolean visible)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				chart.getLegend().setVisible(visible);// .removeLegend();//
			}
		});
		// chart.removeLegend();// chart.getLegend().visible = (visible);// .visible.getLegend().setVisible(visible);
	}

	private void create()
	{
		XYDataset dataset = createDataset();
		// chart = (JFreeChart) XMLParser.getObject(new File("chart/Chart.xml"));
		// XYPlot p = (XYPlot) chart.getPlot();
		// p.setDataset(dataset);
		createChart(dataset);
		final SwingNode swingNode = new SwingNode();
		// swingNode.setContent(content);
		createSwingContent(swingNode);

		pane.setCenter(swingNode);
	}

	private void createSwingContent(final SwingNode swingNode)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				XYDataset dataset = createDataset();
				JFreeChart chart = createChart(dataset);
				XYPlot plot = (XYPlot) chart.getPlot();
				panel = new ChartPanel(chart);
				swingNode.setContent(panel);
			}
		});
	}

	@Override
	public BorderPane getMainPane()
	{
		// TODO Auto-generated method stub
		return pane;
	}

	public ChartActions getChartAction(ActionDefinition action)
	{
		ChartActions chartAction = null;
		try
		{
			chartAction = (ChartActions) action;

		} catch (Exception notChartAction)
		{

		}
		return chartAction;
	}

	@Override
	public void handleAction(ActionDefinition action, Object choice)
	{
		// Console.out.debug(action.toString());
		ChartActions chartAction = getChartAction(action);
		if (chartAction != null)
		{
			try
			{
				switch (chartAction)
				{
				case LEGEND_SHOW:
					toggleLegendVisibility(true);
					break;
				case LEGEND_HIDE:
					toggleLegendVisibility(false);
					break;
				case SELECT_X_AXIS:
				{
					props.setxFilter(choice.toString());
					create();
					break;
				}
				case SELECT_Y_AXIS:
				{
					props.getyFilters().clear();
					props.addToYFilter(choice.toString());
					create();
					break;
				}
				case CLEAR_X_AXIS:
				{
					props.clearXAxis();
					create();
					break;
				}
				case CLEAR_Y_AXIS:
				{
					props.clearYAxis();
					create();
					break;
				}
				case CLEAR_BOTH_AXIS:
				{
					props.clearXYAxis();
					create();
					break;
				}
				}
			} catch (Exception notChartAction)
			{

			}
		}
	}

	private void setupMenus()
	{
		setupAxisMenu();

	}

	private void setupAxisMenu()
	{
		ArrayList<String> axisOptions = getFieldNames();// new ArrayList<String>();
		// environmentOperator.getLoadedEnvironment().get().getData().getAllStateNames();
		axisOptions.add(ChartProperties.simulationTimeVarName);
		OneClickSelectionMenu xAxisSelection = new OneClickSelectionMenu("X Axis", "X",
		ObjectCloner.deepInstanceClone(axisOptions), this, ChartActions.SELECT_X_AXIS);
		OneClickSelectionMenu yAxisSelection = new OneClickSelectionMenu("Y Axis", "Y",
		ObjectCloner.deepInstanceClone(axisOptions), this, ChartActions.SELECT_Y_AXIS);
		Menu axis = (Menu) getMenuItemFromDefinition(ChartActions.AXIS);// (xAxisSelection, ChartActions.SELECT_X_AXIS);
		ArrayList<MenuItem> remove = new ArrayList<MenuItem>();
		for (MenuItem axisItem : axis.getItems())
		{
			if (axisItem.getText().equals("Select X") || axisItem.getText().equals("Select Y"))
			{
				remove.add(axisItem);
			}
		}
		axis.getItems().removeAll(remove);
		axis.getItems().add(0, yAxisSelection);
		axis.getItems().add(1, xAxisSelection);

	}

	@Override
	public ArrayList<MenuItem> getMenuItems()
	{
		// TODO Auto-generated method stub
		return this.getRootMenuItems();
	}

	@Override
	public Object respondToEvent(Object arg0)
	{
		// TODO Auto-generated method stub
		handleAction((ActionDefinition) this.menuItemSelected.get(), arg0);
		return null;
	}

}
