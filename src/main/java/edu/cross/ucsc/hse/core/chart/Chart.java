package edu.cross.ucsc.hse.core.chart;

import com.be3short.io.format.ImageFormat;
import com.be3short.obj.modification.ObjectCloner;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import edu.ucsc.cross.hse.core.environment.Environment;
import edu.ucsc.cross.hse.core.io.Console;
import edu.ucsc.cross.hse.core.task.TaskManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import org.jfree.chart.ChartColor;

public class Chart
{

	// Constants
	public static final String EMPTY = " ";

	// Layout Configuration
	private Integer[][] grid;
	private Double width;
	private Double height;
	private String mainTitle;

	// Individual Chart Configuration
	private HashMap<Integer, SubChart> subPlots;

	// Font config
	private HashMap<LabelType, LabelProperties> fonts;

	// Grid Configuration
	private boolean showXGridLines;
	private boolean showYGridLines;

	// Default Line Rendering Configuration
	private Stroke flowStroke;
	private Stroke jumpStroke;

	// General Series Rendering Configuration
	private ArrayList<Paint> seriesColors = this.defaultSeriesColors();
	private ArrayList<Stroke> seriesStrokes = new ArrayList<Stroke>();

	// User defined labels (in development)
	// private Integer[][] labels;
	// private HashMap<Integer, FreeLabel> extraLabels;

	public String getMainTitle()
	{
		return mainTitle;
	}

	private void initializeSubPlots()
	{
		Set<Integer> subPlotIndexes = getChartLocations().keySet();
		for (Integer sub : subPlotIndexes)
		{
			if (!subPlots.containsKey(sub))
			{
				subPlots.put(sub, new SubChart());
			}
		}
	}

	public Chart(Double width, Double height)
	{
		subPlots = new HashMap<Integer, SubChart>();
		fonts = LabelType.getDefaultMap();
		grid = new Integer[][]
		{
				{ 0 } };
		// labels = new Integer[][]
		// {
		// { 0 } };
		// extraLabels = new HashMap<Integer, FreeLabel>();
		this.width = width;
		this.height = height;
		showXGridLines = true;
		showYGridLines = true;
		float dash1[] =
		{ 2.0f };

		jumpStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dash1, 2.0f);
		flowStroke = new BasicStroke(1.5f);
		initializeSubPlots();
	}

	public void addMainTitle(String main_title, Font title_font)
	{
		mainTitle = main_title;
		if (title_font != null)
		{
			fonts.get(LabelType.MAIN_TITLE).set(title_font);
		}
	}

	public void setLayout(Integer[][] layout)
	{

		grid = layout;
		initializeSubPlots();
	}

	public SubChart sub(Integer index)
	{
		if (subPlots.containsKey(index))
		{
			return subPlots.get(index);
		} else
		{
			Console.warn("sub plot " + index + " does not exist");
			return new SubChart();
		}
	}

	public HashMap<Integer, Integer[][]> getChartLocations()
	{
		HashMap<Integer, Integer[][]> charts = new HashMap<Integer, Integer[][]>();

		for (int rowIndex = 0; rowIndex < grid.length; rowIndex++)
		{
			for (int colIndex = 0; colIndex < grid[0].length; colIndex++)
			{
				if (!charts.containsKey(grid[rowIndex][colIndex]))
				{
					charts.put(grid[rowIndex][colIndex], clearNonGrid(grid, grid[rowIndex][colIndex]));
				}
			}

		}
		return charts;
	}

	public Double measureFont()
	{
		Double height = 0.0;
		if (mainTitle != null)
		{

			height = measureFont(fonts.get(LabelType.MAIN_TITLE).getFont());
		}
		return height;
	}

	public Double measureFont(Font font)
	{
		Double height = 0.0;

		Graphics graphics = new VectorGraphics2D();// (int) 100, (int) 100, (int) 400, (int) 400);
		// get metrics from the graphics
		FontMetrics metrics = graphics.getFontMetrics(font);
		// get the height of a line of text in this
		// font and render context
		int hgt = metrics.getHeight();
		// get the advance of my text in this font
		// and render context
		// calculate the size of a box to hold the
		// text with some padding.
		Dimension size = new Dimension(0 + 2, hgt + 2);
		height = size.getHeight() + 8;

		return height;
	}

	private Integer[][] clearNonGrid(Integer[][] gridz, Integer index)
	{
		Integer[][] grid = (Integer[][]) ObjectCloner.xmlClone(gridz);
		for (int rowIndex = 0; rowIndex < grid.length; rowIndex++)
		{
			for (int colIndex = 0; colIndex < grid[0].length; colIndex++)
			{
				if (!(grid[rowIndex][colIndex].equals(index)))
				{
					grid[rowIndex][colIndex] = -1;
				}
			}

		}
		return grid;
	}

	public Paint getLabelPaint(LabelType type)
	{
		if (fonts.containsKey(type))
		{
			// return //fonts.get(type).fill;
		}
		return Color.BLACK;
	}

	public boolean getLabelVisibility(LabelType type)
	{
		if (fonts.containsKey(type))
		{
			return true;// fonts.get(type).show;
		}
		return false;
	}

	public Font getLabelFont(LabelType type)
	{
		String family = LabelProperties.defaultFont.getFamily();
		Integer style = LabelProperties.defaultFont.getStyle();
		Integer size = LabelProperties.defaultFont.getStyle();
		if (fonts.containsKey(type))
		{
			return fonts.get(type).getFont();

		}
		Font font = new Font(family, style, size);
		return font;
	}

	public HashMap<LabelType, LabelProperties> getFonts()
	{
		return fonts;
	}

	public void setFontMap(HashMap<LabelType, LabelProperties> fonts)
	{
		this.fonts = fonts;
	}

	public LabelProperties editFonts(LabelType type)
	{

		return fonts.get(type);

	}

	// public void plot(Environment envi)
	// {
	// ChartView pv = new ChartView(envi, this, TaskManager.createStage());
	// }
	public void plot(Environment envi)
	{
		new ChartView(envi, this, TaskManager.createStage());
	}

	public void plot(Environment envi, String path, ImageFormat file_format)
	{
		new ChartView(envi, this, TaskManager.createStage(), file_format.createFileSpecs(path));
	}

	public Double getWidth()
	{
		return width;
	}

	public void setWidth(Double width)
	{
		this.width = width;
	}

	public Double getHeight()
	{
		return getHeight(false);
	}

	public Double getHeight(boolean adjusted)
	{
		return height - measureFont();
	}

	public void setHeight(Double height)
	{
		this.height = height;
	}

	public Boolean getShowXGridLines()
	{
		return showXGridLines;
	}

	public void setShowXGridLines(boolean showXGridLines)
	{
		this.showXGridLines = showXGridLines;
	}

	public Boolean getShowYGridLines()
	{
		return showYGridLines;
	}

	public void setShowYGridLines(boolean showYGridLines)
	{
		this.showYGridLines = showYGridLines;
	}

	public Stroke getFlowStroke()
	{
		return flowStroke;
	}

	public void setFlowStroke(Stroke flowStroke)
	{
		this.flowStroke = flowStroke;
	}

	public Stroke getJumpStroke()
	{
		return jumpStroke;
	}

	public void setJumpStroke(Stroke jumpStroke)
	{
		this.jumpStroke = jumpStroke;
	}

	private ArrayList<Paint> defaultSeriesColors()
	{

		return new ArrayList<Paint>(Arrays.asList(ChartColor.createDefaultPaintArray()));
	}

	public Paint getSeriesColor(Integer index)
	{
		Integer adj = Math.floorMod(index, seriesColors.size() - 1);
		return seriesColors.get(adj);
	}

	public void setSeriesColors(ArrayList<Paint> colors)
	{
		seriesColors = colors;
	}
	// public Double getHeightChange(Integer chart_index)
	// {
	// adjustLabelGrid();
	// Double off = 0.0;
	// Double[] offset = computeHeightOffsets();
	// for (int colIndex = 0; colIndex < grid[0].length; colIndex++)
	// {
	//
	// boolean includeRow = false;
	// for (int rowIndex = 0; rowIndex < grid.length; rowIndex++)
	// {
	//
	// if ((grid[rowIndex][colIndex].equals(chart_index)))
	// {
	// includeRow = true;
	// }
	// }
	// if (includeRow)
	// {
	// if (offset[colIndex] > off)
	// {
	// off = offset[colIndex];
	// }
	// }
	//
	// }
	// return off;
	//
	// }

	public ArrayList<Stroke> getSeriesStrokes()
	{
		return seriesStrokes;
	}

	public void setSeriesStrokes(ArrayList<Stroke> seriesStrokes)
	{
		this.seriesStrokes = seriesStrokes;
	}

	// public Double getHeightOffset(Integer chart_index)
	// {
	// adjustLabelGrid();
	// Double off = 0.0;
	// Double[] offset = computeHeightOffsets();
	// for (int colIndex = 0; colIndex < grid[0].length; colIndex++)
	// {
	//
	// boolean includeRow = false;
	// for (int rowIndex = 0; rowIndex < grid.length; rowIndex++)
	// {
	//
	// if ((grid[rowIndex][colIndex].equals(chart_index)))
	// {
	// if ()
	// includeRow = true;
	// }
	// }
	// if (includeRow)
	// {
	// if (offset[colIndex] > off)
	// {
	// off = offset[colIndex];
	// }
	// }
	//
	// }
	// return off;
	//
	// }
	//
	// private Double[] computeHeightOffsets()
	// {
	// Double[] heightOffsets = new Double[grid[0].length];
	//
	// for (int i = 0; i < heightOffsets.length; i++)
	// {
	// heightOffsets[i] = 0.0;
	// }
	//
	// for (int colIndex = 0; colIndex < grid[0].length; colIndex++)
	// {
	// for (int rowIndex = 0; rowIndex < grid.length; rowIndex++)
	// {
	// if ((labels[rowIndex][colIndex] > 0))
	// {
	// try
	// {
	// System.out.println(labels[rowIndex][colIndex]);
	// measureFont(extraLabels.get(labels[rowIndex][colIndex]).getFont());
	// heightOffsets[colIndex] += measureFont(extraLabels.get(labels[rowIndex][colIndex]).getFont());
	// } catch (Exception noLabel)
	// {
	// noLabel.printStackTrace();
	// }
	// }
	// }
	// }
	// System.out.println(XMLParser.serializeObject(heightOffsets));
	// return heightOffsets;
	//
	// }
	// private void adjustLabelGrid()
	// {
	// Integer[][] newLabelGrid = (Integer[][]) ObjectCloner.xmlClone(grid);
	// for (int rowIndex = 0; rowIndex < grid.length; rowIndex++)
	// {
	// for (int colIndex = 0; colIndex < grid[0].length; colIndex++)
	// {
	// try
	// {
	// Integer label = labels[rowIndex][colIndex];
	// newLabelGrid[rowIndex][colIndex] = label;
	// } catch (Exception e)
	// {
	// newLabelGrid[rowIndex][colIndex] = 0;
	// }
	//
	// }
	//
	// }
	// labels = newLabelGrid;
	// }
	//
	// public HashMap<Integer, FreeLabel> getExtraLabels()
	// {
	// return extraLabels;
	// }
	//
	// public void addLabels(Integer[][] layout, FreeLabel[] labels)
	// {
	//
	// this.labels = layout;
	// for (Integer i = 0; i < labels.length; i++)
	// {
	// extraLabels.put(i + 1, labels[i]);
	// }
	// }
	//
	// public HashMap<Integer, Integer[][]> getLabelLocations()
	// {
	// HashMap<Integer, Integer[][]> charts = new HashMap<Integer, Integer[][]>();
	//
	// for (int rowIndex = 0; rowIndex < labels.length; rowIndex++)
	// {
	// for (int colIndex = 0; colIndex < labels[0].length; colIndex++)
	// {
	// if (labels[rowIndex][colIndex] > 0)
	// {
	// if (!charts.containsKey(labels[rowIndex][colIndex]))
	// {
	// charts.put(labels[rowIndex][colIndex], clearNonGrid(grid, grid[rowIndex][colIndex]));
	// }
	// }
	// }
	//
	// }
	// return charts;
	// }
	//
	// public HashMap<Integer, Integer> getLabelCharts()
	// {
	// HashMap<Integer, Integer> charts = new HashMap<Integer, Integer>();
	//
	// for (int rowIndex = 0; rowIndex < labels.length; rowIndex++)
	// {
	// for (int colIndex = 0; colIndex < labels[0].length; colIndex++)
	// {
	// if (labels[rowIndex][colIndex] > 0)
	// {
	// if (!charts.containsKey(labels[rowIndex][colIndex]))
	// {
	// charts.put(labels[rowIndex][colIndex], grid[rowIndex][colIndex]);
	// }
	// }
	// }
	//
	// }
	// return charts;
	// }

}
