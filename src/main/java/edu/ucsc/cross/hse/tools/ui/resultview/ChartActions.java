package edu.ucsc.cross.hse.tools.ui.resultview;

import com.be3short.jfx.event.menu.ActionDefinition;

public enum ChartActions implements ActionDefinition
{

	SELECT_CHART_TYPE(
		"Chart Type",
		VisualizationType.LINE_CHART,
		VisualizationType.SCATTER_CHART),

	SELECT_X_AXIS(
		"Select X"),
	SELECT_Y_AXIS(
		"Select Y"),
	CLEAR_X_AXIS(
		"X Axis"),
	CLEAR_Y_AXIS(
		"Y Axis"),
	CLEAR_BOTH_AXIS(
		"X and Y"),
	CLEAR_AXIS(
		"Clear",
		CLEAR_X_AXIS,
		CLEAR_Y_AXIS,
		CLEAR_BOTH_AXIS),
	AXIS(
		"Axis",
		SELECT_X_AXIS,
		SELECT_Y_AXIS,
		CLEAR_AXIS),
	LEGEND_SHOW(
		"Show"),
	LEGEND_HIDE(
		"Hide"),
	TOGGLE_LEGEND_VISIBILITY(
		"Legend",
		LEGEND_SHOW,
		LEGEND_HIDE),

	VIEW(
		"View",
		TOGGLE_LEGEND_VISIBILITY),
	EDIT(
		"Edit",
		SELECT_CHART_TYPE);

	public final String label;

	public final ActionDefinition[] subMenus;

	private ChartActions(String label, ActionDefinition... sub_menus)
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

		return new ActionDefinition[]
		{ AXIS, VIEW, EDIT };
	}

}
