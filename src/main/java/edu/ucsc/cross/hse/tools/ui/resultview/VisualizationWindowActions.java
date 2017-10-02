package edu.ucsc.cross.hse.tools.ui.resultview;

import com.be3short.jfx.event.menu.ActionDefinition;

public enum VisualizationWindowActions implements ActionDefinition
{

	FILTER_RESULTS(
		"Pause"),

	INITIALIZE_ENVIRONMENT(
		"Initialize Environment"),
	CLEAR_CONSOLE(
		"Clear Console"),
	CONSOLE(
		"Console",
		CLEAR_CONSOLE),
	SAVE_WINDOW_SVG(
		"SVG"),
	SAVE_WINDOW(
		"Window",
		SAVE_WINDOW_SVG),

	SAVE_ENVIRONMENT(
		"Environment"),
	SAVE(
		"Save",
		SAVE_WINDOW,
		SAVE_ENVIRONMENT),

	LOAD_ENVIRONMENT(

		"Environment"),
	LOAD(
		"Load",

		LOAD_ENVIRONMENT),
	CLOSE(
		"Close"),
	REFRESH(
		"Refresh"),
	WINDOW(
		"Window",
		REFRESH,
		CLOSE),
	EDIT(
		"Edit"),
	SHOW_PANE_MENUS(
		"Show"),
	HIDE_PANE_MENUS(
		"Hide"),
	VIEW_PANE_MENUS(
		"Pane Menus",
		SHOW_PANE_MENUS,
		HIDE_PANE_MENUS),
	VIEW(
		"View",
		VIEW_PANE_MENUS),
	FILE(
		"File",
		SAVE,
		LOAD);

	public final String label;

	public final ActionDefinition[] subMenus;

	private VisualizationWindowActions(String label, ActionDefinition... sub_menus)
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
		{ FILE, VIEW, WINDOW };
	}

}
