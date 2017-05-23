package edu.ucsc.cross.hybrid.env.core.classifications;

import java.util.ArrayList;

import edu.ucsc.cross.hybrid.env.core.elements.Data;

public interface GroupClass
{

	public String getTitle();

	public ArrayList<ComponentClass> getContents();

	public boolean contains(Data type);

}
