package edu.ucsc.cross.hse.tools.ui.resultview;

import com.be3short.jfx.event.menu.ActionDefinition;
import com.be3short.jfx.image.ImageIOFormat;

public enum VisualizationPaneActions implements ActionDefinition
{

	SAVE_WINDOW(
		"Window"),
	CLOSE_PANE(
		""),
	SPLIT_HORIZONTALLY(
		"Split Horizontally"),
	SPLIT_VERTICALLY(
		"Split Vertically"),
	SPLIT(
		"Split",
		SPLIT_HORIZONTALLY,
		SPLIT_VERTICALLY),
	FILTER_RESULTS(
		"Pause"),
	TOGGLE_LEGEND_VISIBILITY(
		"Stop"),

	EDIT_PANE(
		"Edit"),
	RESET_ENVIRONMENT(
		"Reset Environment"),
	INITIALIZE_ENVIRONMENT(
		"Initialize Environment"),
	CLEAR_CONSOLE(
		"Clear Console"),
	SAVE(
		"Save",

		SAVE_WINDOW),
	CLOSE(
		"Close"),
	EDIT(
		"Edit"),
	FILE(
		"Pane",
		SAVE,
		SPLIT,
		CLOSE);

	public final String label;

	public final ActionDefinition[] subActions;

	private VisualizationPaneActions(String label, ActionDefinition... sub_menus)
	{
		this.label = label;
		subActions = sub_menus;
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
		return subActions;
	}

	@Override
	public ActionDefinition[] rootItems()
	{
		// TODO Auto-generated method stub
		return new ActionDefinition[]
		{ FILE };
	}

}