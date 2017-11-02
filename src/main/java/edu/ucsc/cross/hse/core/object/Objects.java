package edu.ucsc.cross.hse.core.object;

public class Objects
{

	private String name;

	public Objects()
	{
		this.name = this.getClass().getSimpleName();
	}

	public ObjectsInfo info()
	{
		return new ObjectsInfo(this);
	}

	public static class ObjectsInfo
	{

		Objects obj;

		public ObjectsInfo(Objects object)
		{
			obj = object;
		}

		public String getName()
		{
			return obj.name;
		}

		public void setName(String name)
		{
			obj.name = name;
		}
	}
}
