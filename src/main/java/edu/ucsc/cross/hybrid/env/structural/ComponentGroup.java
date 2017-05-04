package edu.ucsc.cross.hybrid.env.structural;

import java.util.ArrayList;

import edu.ucsc.cross.hybrid.env.core.components.Data;

public interface ComponentGroup
{

	public String getTitle();

	public ArrayList<ComponenDefinition> getContents();

	public boolean contains(Data type);

}
