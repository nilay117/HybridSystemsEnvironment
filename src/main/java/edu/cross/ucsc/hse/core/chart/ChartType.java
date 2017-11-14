package edu.cross.ucsc.hse.core.chart;

public enum ChartType
{
	LINE(
		true,
		false),
	SCATTER(
		false,
		true),
	SCATTER_WITH_LINE(
		true,
		true);

	public final boolean renderLines;
	public final boolean renderShapes;

	private ChartType(boolean lines, boolean shapes)
	{
		renderLines = lines;
		renderShapes = shapes;
	}
}
