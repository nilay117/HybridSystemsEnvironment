package edu.ucsc.cross.hse.core.object;

public class ObjectSet
{

	private String name;

	public ObjectsInfo extension()
	{
		return new ObjectsInfo(this);
	}

	public ObjectSet()
	{
		this.name = this.getClass().getSimpleName();
	}

	public static class ObjectsInfo
	{

		ObjectSet obj;

		public String getName()
		{
			return obj.name;
		}

		public void setName(String name)
		{
			obj.name = name;
		}

		public ObjectsInfo(ObjectSet object)
		{
			obj = object;
		}
	}
}
