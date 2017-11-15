package edu.cross.ucsc.hse.core.chart;

import com.be3short.data.cloning.ObjectCloner;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.awt.Color;
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
			for (DataSeries<?> data : data.getGlobalStateData())
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
					xS = data.getSeriesWithSameParent(properties().getxDataSelection(), this.data.getGlobalStateData());
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
					String label = this.data.getStateNames().get(data.getParentID());
					// defaultValue).getLegendLabel(data);//
					// getLegendLabel(data,
					// names);
					names.add(label);
					XYSeries s1 = new XYSeries(label, false);
					s1.setDescription(label);
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
		for (DataSeries<?> e : data.getGlobalStateData())
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

	JFreeChart createChart(XYDataset dataset)
	{
		if (chartProps.chartTemplate != null)
		{

			chart = (JFreeChart) ObjectCloner.xmlClone(chartProps.chartTemplate);
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
		} else
		{
			PlotOrientation orientation = PlotOrientation.VERTICAL;
			chart = ChartFactory.createXYLineChart("", "", "", dataset, orientation, true, true, true);
			chart.getXYPlot().getDomainAxis().setAxisLineVisible(false);
			chart.getXYPlot().getRangeAxis().setAxisLineVisible(false);
			chart.getXYPlot().setDomainGridlinesVisible(chartProps.getShowXGridLines());
			chart.getXYPlot().setRangeGridlinesVisible(chartProps.getShowYGridLines());
			chart.getXYPlot().setDomainGridlinePaint(Color.GRAY);
			chart.getXYPlot().setRangeGridlinePaint(Color.GRAY);
			// chart.getXYPlot().getRangeAxis().setAutoTickUnitSelection(false);
			// chart.getXYPlot().getDomainAxis().setAutoTickUnitSelection(false);
			chart.getXYPlot().getRangeAxis().setMinorTickCount(4);
			chart.setBackgroundPaint(null);
			chart.getLegend().setBackgroundPaint(null);
			chart.getXYPlot().setBackgroundPaint(null);
			chart.setAntiAlias(true);
			chart.getLegend().setFrame(BlockBorder.NONE);
			chart.getLegend().setHorizontalAlignment(HorizontalAlignment.CENTER);
			chart.getLegend().setVerticalAlignment(VerticalAlignment.CENTER);
			chart.setAntiAlias(false);
		}

		chart.setTitle(properties().getTitle());
		chart.getXYPlot().getDomainAxis().setLabel(properties().getxAxisLabel());
		chart.getXYPlot().getRangeAxis().setLabel(properties().getyAxisLabel());
		chart.getLegend().setVisible(properties().isDisplayLegend());

		return chart;
	}

	private void createSwingContent()
	{
		final SwingNode swingNode = new SwingNode();

		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				XYDataset dataset = null;//
				if (data != null)
				{
					dataset = createDataset();
				}
				createChart(dataset);
				panel = new ChartPanel(chart);
				swingNode.setContent(panel);// panel);
			}
		});

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
		return chartProps.sub(chartIndex);
	}

	Double measureFont()
	{
		Double height = 0.0;
		if (chartProps.getMainTitle() != null)
		{

			height = LabelProperties.measureFont(chartProps.getFonts().get(LabelType.MAIN_TITLE).getFont());
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
}
