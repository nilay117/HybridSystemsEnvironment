package edu.cross.ucsc.hse.core.chart;

public class SubChart
{

	// Labels
	String xAxisLabel;
	String yAxisLabel;
	String title;

	// Visibility
	boolean displayLegend;

	// Data
	String xDataSelection;
	String yDataSelection;

	SubChart()
	{

	}

	public String getxAxisLabel()
	{
		return xAxisLabel;
	}

	public void setxAxisLabel(String xAxisLabel)
	{
		this.xAxisLabel = xAxisLabel;
	}

	public String getyAxisLabel()
	{
		return yAxisLabel;
	}

	public void setyAxisLabel(String yAxisLabel)
	{
		this.yAxisLabel = yAxisLabel;
	}

	public void setAxisSelections(String x_selection, String y_selection)
	{
		xDataSelection = x_selection;
		yDataSelection = y_selection;
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

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String mainTitle)
	{
		this.title = mainTitle;
	}

	public boolean isDisplayLegend()
	{
		return displayLegend;
	}

	public void setDisplayLegend(boolean displayLegend)
	{
		this.displayLegend = displayLegend;
	}

	public String getxDataSelection()
	{
		return xDataSelection;
	}

	public void setxDataSelection(String xDataSelection)
	{
		this.xDataSelection = xDataSelection;
	}

	public String getyDataSelection()
	{
		return yDataSelection;
	}

	public void setyDataSelection(String yDataSelection)
	{
		this.yDataSelection = yDataSelection;
	}
}
