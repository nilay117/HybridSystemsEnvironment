package edu.ucsc.cross.hse.tools.ui.resultview;

import com.be3short.jfx.event.menu.ActionDefinition;

public enum VisualizationType implements ActionDefinition
{

	LINE_CHART(
		"Line Chart"),
	SCATTER_CHART(
		"Scatter Chart");

	public final String label;

	public final ActionDefinition[] subMenus;

	public static final String simulationTimeDataHandle = "Simulation Time";

	private VisualizationType(String label, ActionDefinition... sub_menus)
	{
		this.label = label;
		subMenus = sub_menus;
	}

	@Override
	public String label()
	{
		// TODO Auto-generated method stub
		return label;
	}

	@Override
	public ActionDefinition[] subMenuItems()
	{
		// TODO Auto-generated method stub
		return subMenus;
	}

	@Override
	public ActionDefinition[] rootItems()
	{
		// TODO Auto-generated method stub
		return new ActionDefinition[]
		{};
	}

	public static VisualizationType defaultVisualizationType()
	{
		return VisualizationType.LINE_CHART;
	}

}