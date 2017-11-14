package edu.cross.ucsc.hse.core.chart;

import com.be3short.io.format.ImageFormat;
import com.be3short.io.xml.XMLParser;
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
import java.util.regex.Pattern;
import org.jfree.chart.ChartColor;

public class ChartProperties
{

	// Labeling
	private String mainTitle;
	private HashMap<LabelType, LabelProperties> fonts;

	// Layout
	private Double height;
	private Double width;

	// Sub Plot Configuration
	private Integer[][] grid;
	private HashMap<Integer, SubChartProperties> subPlots;

	// Data Rendering Configuration
	private Stroke flowStroke; // default flow stroke to be used whenever stroke is not specified
	private Stroke jumpStroke;// default flow stroke to be used when stroke is not specified
	private ArrayList<Paint> defaultSeriesColors; // collection of series colors used to render lines

	private HashMap<String, ArrayList<Paint>> assignedColors;
	private HashMap<String, ArrayList<Stroke>> assignedStrokes;

	// Chart Element Configuration
	private boolean showXGridLines;
	private boolean showYGridLines;

	public void addMainTitle(String main_title, Font title_font)
	{
		mainTitle = main_title;
		if (title_font != null)
		{
			fonts.get(LabelType.MAIN_TITLE).set(title_font);
		}
	}

	public void assignColors(String label, Paint... colors)
	{
		if (!assignedColors.containsKey(label))
		{
			assignedColors.put(label, new ArrayList<Paint>());
		}
		for (Paint color : colors)
		{
			assignedColors.get(label).add(color);
		}
	}

	public void assignStrokes(String label, Stroke... colors)
	{
		if (!assignedStrokes.containsKey(label))
		{
			assignedStrokes.put(label, new ArrayList<Stroke>());
		}
		for (Stroke color : colors)
		{
			assignedStrokes.get(label).add(color);
		}
	}

	public void createChart(Environment envi)
	{
		System.out.println(XMLParser.serializeObject(envi.getData()));
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

		return getSeriesColor(index, null);
	}

	public Paint getSeriesColor(Integer index, String series_name)
	{
		Integer adj = Math.floorMod(index, defaultSeriesColors.size() - 1);
		if (series_name != null)
		{
			for (String assignedColor : assignedColors.keySet())
			{
				if (series_name.contains(assignedColor))
				{
					String valueSplit[] = series_name.split(Pattern.quote("("));
					String valString = valueSplit[valueSplit.length - 1].split(Pattern.quote(")"))[0];
					ArrayList<Paint> colors = assignedColors.get(assignedColor);
					Integer val = 0;
					try
					{
						val = Integer.parseInt(valString) - 1;

					} catch (Exception e)
					{

					}
					Integer adj2 = Math.floorMod(val, colors.size());
					try
					{
						Paint col = assignedColors.get(assignedColor).get(adj2);
						return col;

					} catch (Exception e)
					{

					}
				}
			}
		}
		return defaultSeriesColors.get(adj);
	}

	public Stroke getSeriesStrokes(String series_name)
	{
		if (series_name != null)
		{
			for (String assignedColor : assignedStrokes.keySet())
			{
				if (series_name.contains(assignedColor))
				{
					String valueSplit[] = series_name.split(Pattern.quote("("));
					String valString = valueSplit[valueSplit.length - 1].split(Pattern.quote(")"))[0];
					ArrayList<Stroke> colors = assignedStrokes.get(assignedColor);
					Integer val = 0;
					try
					{
						val = Integer.parseInt(valString) - 1;

					} catch (Exception e)
					{
						val = 0;
					}
					Integer adj2 = Math.floorMod(val, colors.size());
					try
					{
						Stroke col = assignedStrokes.get(assignedColor).get(adj2);
						return col;

					} catch (Exception e)
					{

					}
				}
			}
		}
		return getBaseFlowStroke();

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
		defaultSeriesColors = colors;
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

	public SubChartProperties sub(Integer index)
	{
		if (subPlots.containsKey(index))
		{
			return subPlots.get(index);
		} else
		{
			Console.warn("sub plot " + index + " does not exist");
			return new SubChartProperties();
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

	private void initialize(Double width, Double height)
	{
		defaultSeriesColors = this.defaultSeriesColors();

		fonts = LabelType.getDefaultMap();

		this.width = width;
		this.height = height;

		showXGridLines = true;
		showYGridLines = true;

		grid = new Integer[][]
		{
				{ 0 } };

		float dash1[] =
		{ 2.0f };

		assignedColors = new HashMap<String, ArrayList<Paint>>();
		assignedStrokes = new HashMap<String, ArrayList<Stroke>>();

		jumpStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dash1, 2.0f);
		flowStroke = new BasicStroke(3.5f);

		subPlots = new HashMap<Integer, SubChartProperties>();
		initializeSubPlots();
	}

	private void initializeSubPlots()
	{
		Set<Integer> subPlotIndexes = ChartOperations.getChartLocations(this).keySet();
		for (Integer sub : subPlotIndexes)
		{
			if (!subPlots.containsKey(sub))
			{
				subPlots.put(sub, new SubChartProperties());
			}
		}
	}

	public ChartProperties(Double width, Double height)
	{
		initialize(width, height);
	}

	public static class ChartOperations
	{

		public static HashMap<Integer, Integer[][]> getChartLocations(ChartProperties properties)
		{
			HashMap<Integer, Integer[][]> charts = new HashMap<Integer, Integer[][]>();

			for (int rowIndex = 0; rowIndex < properties.grid.length; rowIndex++)
			{
				for (int colIndex = 0; colIndex < properties.grid[0].length; colIndex++)
				{
					if (!charts.containsKey(properties.grid[rowIndex][colIndex]))
					{
						charts.put(properties.grid[rowIndex][colIndex],
						properties.clearNonGrid(properties.grid, properties.grid[rowIndex][colIndex]));
					}
				}

			}
			return charts;
		}

	}

	// Constants
	public static final String EMPTY = " ";
}
