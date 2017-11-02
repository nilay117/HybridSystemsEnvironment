package edu.cross.ucsc.hse.core.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;

public class LabelProperties
{

	public static final Font defaultFont = new Font("Times", Font.PLAIN, 12);

	private Font font;
	private Paint color;
	private Boolean visibility;

	public LabelProperties(boolean visibility, Font font)
	{
		this.font = font;
		color = Color.BLACK;
		this.visibility = visibility;
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

	public Font getFont()
	{
		return font;
	}

	public void setFont(Font font)
	{
		this.font = font;
	}

	public Paint getColor()
	{
		return color;
	}

	public void setColor(Paint color)
	{
		this.color = color;
	}

	public Boolean getVisibility()
	{
		return visibility;
	}

	public void setVisibility(Boolean visibility)
	{
		this.visibility = visibility;
	}
}
