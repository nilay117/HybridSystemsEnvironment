package edu.cross.ucsc.hse.core.chart;

import java.awt.Font;
import java.util.HashMap;

public enum LabelType
{
	MAIN_TITLE(
		false,
		true,
		new Font("Tahoma", Font.BOLD, 24)),
	TITLE(
		false,
		true,
		new Font("Tahoma", Font.BOLD, 18)),
	DOMAIN_AXIS_LABEL(
		true,
		true,
		new Font("Tahoma", Font.BOLD, 14)),
	RANGE_AXIS_LABEL(
		true,
		true,
		new Font("Tahoma", Font.BOLD, 14)),
	DOMAIN_AXIS_TICK_LABEL(
		true,
		false,
		new Font("Tahoma", Font.PLAIN, 12)),
	RANGE_AXIS_TICK_LABEL(
		true,
		false,
		new Font("Tahoma", Font.PLAIN, 12)),
	LEGEND_ITEM(
		true,
		false,
		new Font("Tahoma", Font.PLAIN, 10)),
	SUB_TITLE(
		false,
		false,
		new Font("Tahoma", Font.BOLD, 16));

	public final boolean defaultVisibility;
	public final boolean variableText;
	public final Font defaultFont;

	private LabelType(boolean default_visibility, boolean variable_text, Font default_font)
	{
		this.defaultVisibility = default_visibility;
		this.variableText = variable_text;
		this.defaultFont = default_font;
	}

	public static HashMap<LabelType, LabelProperties> getDefaultMap()
	{
		HashMap<LabelType, LabelProperties> defaults = new HashMap<LabelType, LabelProperties>();
		for (LabelType type : LabelType.values())
		{
			defaults.put(type, new LabelProperties(type.defaultVisibility, type.defaultFont));

		}
		return defaults;
	}

	public static HashMap<LabelType, String> getDefaultLabelStringMap()
	{
		HashMap<LabelType, String> defaults = new HashMap<LabelType, String>();
		for (LabelType type : LabelType.values())
		{
			if (type.variableText)
			{
				defaults.put(type, "");
			}
		}
		return defaults;
	}
}
