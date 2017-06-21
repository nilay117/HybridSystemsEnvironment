package edu.ucsc.cross.hse.core2.framework.component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class Zipper
{

	public static byte[] compressDataGZip(String string)
	{
		byte[] dataToCompress = string.getBytes(StandardCharsets.ISO_8859_1);
		byte[] compressedData = null;
		try
		{
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(dataToCompress.length);
			try
			{
				GZIPOutputStream zipStream = new GZIPOutputStream(byteStream);
				try
				{
					zipStream.write(dataToCompress);
				} finally
				{
					zipStream.close();
				}
			} finally
			{
				byteStream.close();
			}

			compressedData = byteStream.toByteArray();
		} catch (Exception e)
		{
			e.printStackTrace();
			compressedData = string.getBytes();
		}
		return compressedData;
	}

	public static enum CompressionFormat
	{
		GZIP,
		NONE;
	}
}