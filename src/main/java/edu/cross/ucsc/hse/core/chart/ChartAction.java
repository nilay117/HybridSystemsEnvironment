package edu.cross.ucsc.hse.core.chart;

import com.be3short.ui.menu.MenuDefinition;
import java.util.Arrays;

public enum ChartAction implements MenuDefinition
{

	OPEN_FORMATTED(
		"Open"),
	OPEN_CUSTOM(
		"Custom"),
	CREATE_FORMATTED(
		"New"),
	EXPORT_SVG(
		"SVG"),
	EXPORT_CONFIGURATION(
		"Configuration"),
	EXPORT_PNG(
		"PNG"),
	EXPORT_JPG(
		"JPEG"),
	EXPORT_GIF(
		"GIF"),
	EXPORT_BMP(
		"BMP"),
	EXPORT_EPS(
		"EPS"),
	EXPORT_IMAGE(
		"Image",
		EXPORT_PNG,
		EXPORT_JPG,
		EXPORT_GIF,
		EXPORT_BMP),
	EXPORT_VECTORED(
		"Vector",
		EXPORT_SVG,
		EXPORT_EPS),

	EXPORT(
		"Export",
		EXPORT_VECTORED,
		EXPORT_IMAGE,
		EXPORT_CONFIGURATION),

	EXPORT_ONLY(
		"Export",
		EXPORT_SVG,
		EXPORT_EPS,
		EXPORT_PNG,
		EXPORT_JPG,
		EXPORT_GIF,
		EXPORT_BMP),
	GENERATE(
		"Generate"),
	MENUS(
		"Menus"),
	ANALYSIS(
		"Results",
		CREATE_FORMATTED,
		OPEN_FORMATTED,
		OPEN_CUSTOM,
		EXPORT,
		GENERATE,
		MENUS);

	public final String label;

	public final MenuDefinition[] subMenus;

	private ChartAction(String label, MenuDefinition... sub_menus)
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
	public MenuDefinition[] subMenuItems()
	{
		// TODO Auto-generated method stub
		return subMenus;
	}

	public boolean isSubMenu(ChartAction action)
	{
		if (Arrays.asList(subMenus).contains(action))
		{
			return true;
		}
		return false;
	}
}
