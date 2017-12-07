package edu.cross.ucsc.hse.core.chart;

import com.be3short.data.cloning.ObjectCloner;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.monitor.DataMonitor;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.BorderPane;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.VerticalAlignment;

public class SubChartView
{

	public ChartPanel panel;
	private JFreeChart chart;
	private Integer chartIndex;
	private ChartConfiguration chartProps;
	private EnvironmentData data;
	private BorderPane pane;
	ChartView ch;
	// Content

	public XYDataset createDataset()
	{
		ArrayList<String> names = new ArrayList<String>();
		XYSeriesCollection dataset = new XYSeriesCollection();
		StandardXYItemRenderer rend = new StandardXYItemRenderer();
		rend.setPlotDiscontinuous(false);
		HashMap<String, XYSeries> ser = new HashMap<String, XYSeries>();
		if (properties().getyDataSelection() != null)
		{
			for (DataSeries<?> data : DataMonitor.getAllDataSeries(data))
			{
				boolean matchesSelection = true;
				DataSeries<?> xS = null;
				// System.out.println(props.fufilsFilters(data));
				if (!properties().getyDataSelection().equals(data.getElementName()))
				// .!fufilsFilters(data))
				{
					matchesSelection = false;
				}
				if (properties().getxDataSelection() != null)
				{
					xS = data.getSeriesWithSameParent(properties().getxDataSelection(),
					DataMonitor.getAllDataSeries(this.data));
					if (xS == null)
					{
						matchesSelection = false;
					}
				}
				matchesSelection = matchesSelection && (data.getAllStoredData().get(0).getClass().equals(Double.class)
				|| data.getAllStoredData().get(0).getClass().equals(double.class));
				if (matchesSelection)
				{
					// \String label = this.data.getStateNames().get(data.getParentID() + data.getElementName());//
					String label = data.getParent().data().getUniqueLabel();// .getUniqueLabel();
					// defaultValue).getLegendLabel(data);//
					// getLegendLabel(data,
					// names);
					names.add(label);
					XYSeries s1 = new XYSeries(label, false, true);
					s1.setDescription(data.getParent().data().getUniqueLabel());
					for (HybridTime ind : this.data.getStoreTimes())
					{
						Double y = (Double) data.getStoredData(ind);
						Double x = ind.getTime();
						if (xS != null)
						{
							x = (Double) xS.getStoredData(ind);
						}
						if (x != null && y != null)
						{
							XYDataItem dat = new XYDataItem(x, y);
							s1.add(dat);
						}
					}
					ser.put(s1.getKey().toString(), s1);

				}
			}
			ArrayList<String> serName = new ArrayList<String>(ser.keySet());
			Collections.sort(serName);
			for (String se : serName)
			{
				dataset.addSeries(ser.get(se));
			}
		}
		return dataset;
	}

	public JFreeChart getChart()
	{
		return chart;
	}

	public ArrayList<String> getFieldNames()
	{
		ArrayList<String> fieldNames = new ArrayList<String>();
		for (DataSeries<?> e : DataMonitor.getAllDataSeries(data))
		{

			if (!fieldNames.contains(e.getElementName()))
			{
				fieldNames.add(e.getElementName());
			}
		}
		return fieldNames;
	}

	public BorderPane getPane()
	{
		return pane;
	}

	public void toggleLegendVisibility(boolean visible)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				chart.getLegend().setVisible(visible);

			}
		});
	}

	public static JFreeChart getDefaultChart()
	{
		JFreeChart chart = ChartFactory.createXYLineChart("", "", "", null, PlotOrientation.VERTICAL, true, true, true);

		chart.getXYPlot().getDomainAxis().setAxisLineVisible(false);
		chart.getXYPlot().getRangeAxis().setAxisLineVisible(false);
		chart.getXYPlot().setDomainGridlinesVisible(true);
		chart.getXYPlot().setRangeGridlinesVisible(true);

		chart.getXYPlot().getDomainAxis().setLabelFont(new Font("Tahoma", Font.PLAIN, 12));
		chart.getXYPlot().getRangeAxis().setLabelFont(new Font("Tahoma", Font.PLAIN, 12));
		chart.getXYPlot().getDomainAxis().setTickLabelFont(new Font("Tahoma", Font.PLAIN, 10));
		chart.getXYPlot().getRangeAxis().setTickLabelFont(new Font("Tahoma", Font.PLAIN, 10));
		chart.getTitle().setFont(new Font("Tahoma", Font.BOLD, 14));
		chart.getLegend().setItemFont(new Font("Tahoma", Font.PLAIN, 10));

		chart.setBackgroundPaint(null);
		chart.getLegend().setBackgroundPaint(null);
		chart.getXYPlot().setBackgroundPaint(null);
		chart.getXYPlot().setDomainGridlinePaint(Color.GRAY);
		chart.getXYPlot().setRangeGridlinePaint(Color.GRAY);

		chart.setAntiAlias(true);

		chart.getLegend().setFrame(BlockBorder.NONE);
		chart.getLegend().setHorizontalAlignment(HorizontalAlignment.CENTER);
		chart.getLegend().setVerticalAlignment(VerticalAlignment.CENTER);

		return chart;
	}

	private void initializeChart(XYDataset dataset)
	{
		try
		{

			chart = (JFreeChart) ObjectCloner.xmlClone(chartProps.getChartTemplate());
			// ObjectCloner.deepInstanceClone(chartProps.chart);
			chart.getXYPlot().setDataset(dataset);
			HybridDataRenderer renderer = new HybridDataRenderer(properties().getChartType(), data, chartProps,
			dataset);// true,

			// XYShapeRenderer renderer = new XYShapeRenderer();

			// false);
			chart.getXYPlot().setRenderer(renderer);
			if (properties().getxDataSelection() == null)
			{
				chart.getXYPlot().getDomainAxis().setRange(data.getEarliestStoredTime().getTime(),
				data.getLastStoredTime().getTime());
			}
			chart.setTitle(properties().getTitle());
			chart.getXYPlot().getDomainAxis().setLabel(properties().getxAxisLabel());
			chart.getXYPlot().getRangeAxis().setLabel(properties().getyAxisLabel());
			chart.getLegend().setVisible(properties().isDisplayLegend());

		} catch (Exception e)
		{

		}

	}

	private void createSwingContent()
	{
		final SwingNode swingNode = new SwingNode();

		// SwingUtilities.invokeLater(new Runnable()
		// {

		// @Override
		// public void run()
		{
			XYDataset dataset = null;//
			if (chart == null)
			{
				if (data != null)
				{
					dataset = createDataset();
				}
				initializeChart(dataset);
			}
			panel = new ChartPanel(chart);
			swingNode.setContent(panel);// panel);
		}
		// });

		pane.setCenter(swingNode);
	}

	private void initialize(EnvironmentData data, BorderPane pane, ChartConfiguration chart_props, String y,
	Integer chart_index, ChartView ch)
	{
		this.ch = ch;
		// props = new ChartProperties();
		this.pane = pane;
		this.data = data;
		this.chartProps = chart_props;
		chartIndex = chart_index;
		// props.setyFilters(y);
		// props.setxFilter(HybridPlot.simTime);
		if (pane != null)
		{
			createSwingContent();
		}
	}

	private SubChartProperties properties()
	{
		return chartProps.chartProperties(chartIndex);
	}

	Double measureFont()
	{
		Double height = 0.0;
		if (chartProps.getMainTitle() != null)
		{

			height = LabelProperties.measureFont(chartProps.getMainTitleFont());
		}
		return height;
	}

	SubChartView(BorderPane pane, ChartConfiguration chart_props, Integer chart_index, ChartView ch)
	{
		initialize(null, pane, chart_props, null, chart_index, ch);
	}

	// Boiler Plate

	SubChartView(EnvironmentData data, BorderPane pane, ChartConfiguration chart_props, Integer chart_index,
	ChartView ch)
	{
		initialize(data, pane, chart_props, null, chart_index, ch);
	}

	SubChartView(EnvironmentData data, BorderPane pane, String y, ChartConfiguration chart_props, Integer chart_index,
	ChartView ch)
	{
		initialize(data, pane, chart_props, y, chart_index, ch);

	}

	SubChartView(JFreeChart multi_chart, BorderPane pane, ChartConfiguration chart_props, ChartView ch)
	{
		chart = multi_chart;
		initialize(data, pane, chart_props, null, 0, ch);

	}
}
