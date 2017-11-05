package edu.cross.ucsc.hse.core.chart;

import com.be3short.io.format.ImageFormat;
import com.be3short.obj.modification.ObjectCloner;
import edu.ucsc.cross.hse.core.environment.Environment;
import edu.ucsc.cross.hse.core.io.Console;
import edu.ucsc.cross.hse.core.task.TaskManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import org.jfree.chart.ChartColor;

public class ChartProperties
{

	// Default Line Rendering Configuration
	private Stroke flowStroke;
	// Font Configuration
	private HashMap<LabelType, LabelProperties> fonts;

	// Layout Configuration
	private Integer[][] grid;
	private Double height;
	private Stroke jumpStroke;
	private String mainTitle;

	// General Series Rendering Configuration
	private ArrayList<Paint> seriesColors = this.defaultSeriesColors();

	private ArrayList<Stroke> seriesStrokes = new ArrayList<Stroke>();

	// Grid Configuration
	private boolean showXGridLines;
	private boolean showYGridLines;

	// Individual Chart Configuration
	private HashMap<Integer, SubChart> subPlots;
	private Double width;

	public void addMainTitle(String main_title, Font title_font)
	{
		mainTitle = main_title;
		if (title_font != null)
		{
			fonts.get(LabelType.MAIN_TITLE).set(title_font);
		}
	}

	public void createChart(Environment envi)
	{
		new ChartView(envi.getData(), this, TaskManager.createStage());
	}

	public void createChart(Environment envi, String path, ImageFormat file_format)
	{
		new ChartView(envi.getData(), this, TaskManager.createStage(), file_format.createFileSpecs(path));
	}

	public LabelProperties editFonts(LabelType type)
	{

		return fonts.get(type);

	}

	public Stroke getBaseFlowStroke()
	{
		return flowStroke;
	}

	public Stroke getBaseJumpStroke()
	{
		return jumpStroke;
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

	public HashMap<LabelType, LabelProperties> getFonts()
	{
		return fonts;
	}

	public Double getHeight()
	{
		return height;
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

	public String getMainTitle()
	{
		return mainTitle;
	}

	public Paint getSeriesColor(Integer index)
	{
		Integer adj = Math.floorMod(index, seriesColors.size() - 1);
		return seriesColors.get(adj);
	}

	public ArrayList<Stroke> getSeriesStrokes()
	{
		return seriesStrokes;
	}

	public Boolean getShowXGridLines()
	{
		return showXGridLines;
	}

	public Boolean getShowYGridLines()
	{
		return showYGridLines;
	}

	public Double getWidth()
	{
		return width;
	}

	public void setFlowStroke(Stroke flowStroke)
	{
		this.flowStroke = flowStroke;
	}

	public void setFontMap(HashMap<LabelType, LabelProperties> fonts)
	{
		this.fonts = fonts;
	}

	public void setHeight(Double height)
	{
		this.height = height;
	}

	public void setJumpStroke(Stroke jumpStroke)
	{
		this.jumpStroke = jumpStroke;
	}

	public void setLayout(Integer[][] layout)
	{

		grid = layout;
		initializeSubPlots();
	}

	public void setSeriesColors(ArrayList<Paint> colors)
	{
		seriesColors = colors;
	}

	public void setSeriesStrokes(ArrayList<Stroke> seriesStrokes)
	{
		this.seriesStrokes = seriesStrokes;
	}

	public void setShowXGridLines(boolean showXGridLines)
	{
		this.showXGridLines = showXGridLines;
	}

	public void setShowYGridLines(boolean showYGridLines)
	{
		this.showYGridLines = showYGridLines;
	}

	public void setWidth(Double width)
	{
		this.width = width;
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

	private ArrayList<Paint> defaultSeriesColors()
	{

		return new ArrayList<Paint>(Arrays.asList(ChartColor.createDefaultPaintArray()));
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

	public ChartProperties(Double width, Double height)
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

	// Constants
	public static final String EMPTY = " ";

	// User defined labels (in development)
	// private Integer[][] labels;
	// private HashMap<Integer, FreeLabel> extraLabels;
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
}
