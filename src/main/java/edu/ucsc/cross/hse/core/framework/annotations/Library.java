package edu.ucsc.cross.hse.core.framework.annotations;

import edu.ucsc.cross.hse.core.framework.component.Component;
import java.util.HashMap;

public class Library
{

	String name;

	HashMap<String, Component> contents;

	public Library(String library_name)
	{
		name = library_name;
		contents = new HashMap<String, Component>();
	}

	public void addContent(String name, Component item)
	{
		contents.put(name, item);
	}

	public void addContent(HashMap<String, Component> content)
	{

	}
}
