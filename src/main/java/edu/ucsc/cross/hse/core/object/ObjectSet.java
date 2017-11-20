package edu.ucsc.cross.hse.core.object;

public class ObjectSet
{

	private String name;
	private String uniqueLabel;
	private Integer address;
	private boolean simulated;

	private boolean saveHistory;

	public ObjectSetAPI data()
	{
		return new ObjectSetAPI(this);
	}

	public ObjectSet()
	{
		this.name = this.getClass().getSimpleName();

	}

	public static class ObjectSetAPI
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

		public String getUniqueLabel()
		{
			return obj.uniqueLabel;
		}

		public Integer getAddress()
		{
			return obj.address;
		}

		public ObjectSetAPI(ObjectSet object)
		{
			obj = object;
		}

		public void setHistorySaved(boolean save)
		{
			obj.saveHistory = save;
		}

		public void setSimulated(boolean simulated)
		{
			obj.simulated = simulated;
		}

		public void setUniqueLabel(String name)
		{
			obj.uniqueLabel = name;
		}

		public void setAddress(Integer address)
		{
			obj.address = address;
		}

		public static boolean isHistorySaved(ObjectSet obj)
		{
			return obj.saveHistory;
		}

		public static boolean isSimulated(ObjectSet obj)
		{
			return obj.simulated;
		}
	}
}
