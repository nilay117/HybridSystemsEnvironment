package edu.ucsc.cross.hybrid.env.core.classification;

import java.util.ArrayList;

import edu.ucsc.cross.hybrid.env.core.components.Data;

public interface DataGroup
{

	public String getTitle();

	public ArrayList<DataType> getContents();

	public boolean contains(Data type);

}
