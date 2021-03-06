package edu.cross.ucsc.hse.core.chart;

public class SubChartProperties
{

	// Display
	ChartType type;
	boolean displayLegend;

	// Labels
	String xAxisLabel;
	String yAxisLabel;
	String title;

	// Data Selection
	String xDataSelection;
	String yDataSelection;

	public ChartType getChartType()
	{
		return type;
	}

	public String getTitle()
	{
		return title;
	}

	public String getxAxisLabel()
	{
		return xAxisLabel;
	}

	public String getxDataSelection()
	{
		return xDataSelection;
	}

	public String getyAxisLabel()
	{
		return yAxisLabel;
	}

	public String getyDataSelection()
	{
		return yDataSelection;
	}

	public boolean isDisplayLegend()
	{
		return displayLegend;
	}

	public void setAxisLabels(String x_label, String y_label)
	{
		xAxisLabel = x_label;
		yAxisLabel = y_label;
	}

	public void setAxisSelectionAndLabels(String x_selection, String y_selection, String x_label, String y_label)
	{
		xAxisLabel = x_label;
		yAxisLabel = y_label;
		xDataSelection = x_selection;
		yDataSelection = y_selection;
	}

	public void setAxisSelections(String x_selection, String y_selection)
	{

		xDataSelection = x_selection;
		yDataSelection = y_selection;
	}

	public void setChartType(ChartType type)
	{
		this.type = type;
	}

	public void setDisplayLegend(boolean displayLegend)
	{
		this.displayLegend = displayLegend;
	}

	public void setTitle(String mainTitle)
	{
		this.title = mainTitle;
	}

	public void setxAxisLabel(String xAxisLabel)
	{
		this.xAxisLabel = xAxisLabel;
	}

	public void setxDataSelection(String xDataSelection)
	{
		this.xDataSelection = xDataSelection;
	}

	public void setyAxisLabel(String yAxisLabel)
	{
		this.yAxisLabel = yAxisLabel;
	}

	public void setyDataSelection(String yDataSelection)
	{
		this.yDataSelection = yDataSelection;
	}

	SubChartProperties()
	{
		title = "";
		yAxisLabel = "";
		xAxisLabel = "";
		type = ChartType.LINE;
	}
}
