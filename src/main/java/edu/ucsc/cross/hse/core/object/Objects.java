package edu.ucsc.cross.hse.core.object;

public class Objects
{

	private String name;

	public ObjectsInfo info()
	{
		return new ObjectsInfo(this);
	}

	public Objects()
	{
		this.name = this.getClass().getSimpleName();
	}

	public static class ObjectsInfo
	{

		Objects obj;

		public String getName()
		{
			return obj.name;
		}

		public void setName(String name)
		{
			obj.name = name;
		}

		public ObjectsInfo(Objects object)
		{
			obj = object;
		}
	}
}
