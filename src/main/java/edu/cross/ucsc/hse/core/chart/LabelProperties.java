package edu.cross.ucsc.hse.core.chart;

import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Paint;

public class LabelProperties
{

	private Paint color;

	private Font font;
	private Boolean visibility;
	public Paint getColor()
	{
		return color;
	}

	public Font getFont()
	{
		return font;
	}

	public Boolean getVisibility()
	{
		return visibility;
	}

	public void set(Font new_font)
	{
		font = new_font;
	}

	public void set(Font font, Paint color, boolean visibility)
	{
		this.font = font;
		this.color = color;
		this.visibility = visibility;
	}

	public void setColor(Paint color)
	{
		this.color = color;
	}

	public void setFont(Font font)
	{
		this.font = font;
	}

	public void setVisibility(Boolean visibility)
	{
		this.visibility = visibility;
	}

	public LabelProperties(boolean visibility, Font font)
	{
		this.font = font;
		color = Color.BLACK;
		this.visibility = visibility;
	}

	public static final Font defaultFont = new Font("Times", Font.PLAIN, 12);

	public static Double measureFont(Font font)
	{
		Double height = 0.0;
	
		Graphics graphics = new VectorGraphics2D();// (int) 100, (int) 100, (int) 400, (int) 400);
		// get metrics from the graphics
		FontMetrics metrics = graphics.getFontMetrics(font);
		// get the height of a line of text in this
		// font and render context
		int hgt = metrics.getHeight();
		// get the advance of my text in this font
		// and render context
		// calculate the size of a box to hold the
		// text with some padding.
		Dimension size = new Dimension(0 + 2, hgt + 2);
		height = size.getHeight() + 8;
	
		return height;
	}
}
