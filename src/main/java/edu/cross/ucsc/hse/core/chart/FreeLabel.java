package edu.cross.ucsc.hse.core.chart;

import java.awt.Font;

public class FreeLabel extends LabelProperties
{

	private String text;
	private LabelPosition position;

	public FreeLabel(String text, LabelPosition position, Font font)
	{
		super(true, font);
		this.text = text;
		this.position = position;
		// TODO Auto-generated constructor stub
	}

	public static enum LabelPosition
	{
		ABOVE,
		BELOW;
	}

	public static FreeLabel above(String text, Font font)
	{
		return new FreeLabel(text, LabelPosition.ABOVE, font);
	}

	public static FreeLabel below(String text, Font font)
	{
		return new FreeLabel(text, LabelPosition.BELOW, font);
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public LabelPosition getPosition()
	{
		return position;
	}

	public void setPosition(LabelPosition position)
	{
		this.position = position;
	}
}
