package edu.ucsc.cross.hse.core.procesing.io;

import java.util.HashMap;

import bs.commons.objects.manipulation.XMLParser;

public class SaveFile
{

	public HashMap<FileComponent, byte[]> fileComponents;
	public HashMap<String, byte[]> data;
	public byte[] dataz;

	public SaveFile(String file_input)
	{
		initialize(file_input);
	}

	private void initialize(String file_input)
	{
		fileComponents = new HashMap<FileComponent, byte[]>();
		if (file_input != null)
		{
			decodeFile(file_input);
		}
	}

	private void decodeFile(String file_input)
	{
		SaveFile decodedFile = (SaveFile) XMLParser.getObjectFromString(file_input);
		for (FileComponent comp : decodedFile.fileComponents.keySet())
		{
			fileComponents.put(comp, decodedFile.fileComponents.get(comp));
		}
		data = decodedFile.data;
		dataz = decodedFile.dataz;
	}
}
