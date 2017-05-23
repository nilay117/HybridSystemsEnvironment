package edu.ucsc.cross.hse.core.processing.data;

import java.util.ArrayList;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

import edu.ucsc.cross.hse.core.component.categorization.CoreDataGroup;
import edu.ucsc.cross.hse.core.component.constructors.Component;
import edu.ucsc.cross.hse.core.component.constructors.DataSet;
import edu.ucsc.cross.hse.core.component.data.Data;
import edu.ucsc.cross.hse.core.processing.management.Environment;
import edu.ucsc.cross.hse.core.processing.management.Processor;

@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class DataCollector extends Processor
{

	public Double getLastStoreTime()
	{
		return lastStoreTime;
	}

	public ArrayList<Data> getDataStates()
	{
		return dataStates;
	}

	private Double lastStoreTime = -10.0;
	private ArrayList<Data> dataStates;

	public DataCollector(Environment processor)
	{
		super(processor);
		dataStates = new ArrayList<Data>();
	}

	public Data getValue(Integer index)
	{
		return dataStates.get(index);
	}

	public void storeData(double t, boolean override_store_time)
	throws MaxCountExceededException, DimensionMismatchException
	{
		if (override_store_time)
		{
			storeData(t);
		} else if (t > lastStoreTime + getSettings().getData().dataStoreIncrement)// settings.dataStoreIncrement)
		{
			lastStoreTime = t;
			storeData(t);
		}
	}

	public void loadStoreStates()
	{
		dataStates.clear();
		for (Component component : super.getEnvironment().getComponents(true))
		{
			try
			{
				Data element = (Data) component;

				if (CoreDataGroup.ALL_STATES.contains(element))
				{
					if (Data.isStored(element))
					{
						dataStates.add(element);
					}
				}
			} catch (Exception notElement)
			{

			}
		}
	}

	public ArrayList<String> getAllPossibleStateElements()
	{
		ArrayList<String> stateElements = new ArrayList<String>();
		for (Data allStates : dataStates)
		{
			if (!stateElements.contains(allStates.getProperties().getName()))
			{
				stateElements.add(allStates.getProperties().getName());
			}
		}
		// System.out.println(stateElements.toString());

		return stateElements;
	}

	public void storeData(Double time)
	{
		for (Data element : dataStates)
		{
			Data.storeValue(element, time);
		}
		// IO.out(getConsole().getDataElementStoreString(time, true));
	}

	public ArrayList<Data> matchingElements(String title)
	{
		ArrayList<Data> datas = new ArrayList<Data>();
		for (Data element : dataStates)
		{
			if (element.getProperties().getName().equals(title))
			{
				datas.add(element);
			}
		}
		return datas;
	}

	public Data matchingElementsInDataSet(Data data, String title)
	{
		for (Component component : getEnvironment().getComponents(true))
		{
			try
			{
				DataSet set = (DataSet) component;
				if (set.getComponents(true).contains(data))
				{
					for (Component subComponent : set.getComponents(true))
					{
						try
						{
							Data dat = (Data) subComponent;
							if (dat.getProperties().getName().equals(title))
							{
								return dat;
							}

						} catch (Exception notData)
						{

						}
					}
				}

			} catch (Exception notDataSet)
			{

			}
		}

		return null;
	}
	// @SuppressWarnings("unchecked")
	// public void storeData(Double time)
	// {
	// if (!times.contains(time))
	// {
	// times.add(time);
	// for (Integer systemIndex : dataStates.keySet())
	// {
	// Element.storeValue(dataStates.get(systemIndex));
	// }
	// }
	// }

	// public Double getValue(Integer time_index, Integer component_index,
	// String value_name)
	// {
	// Double val = null;
	// try
	// {
	// //val = data.get(component_index).get(value_name).get(time_index);
	// } catch (Exception e)
	// {
	//
	// }
	// return val;
	// }
	//
	// public ArrayList<Double> getTimes()
	// {
	// return times;
	// }
	//
	// public HashMap<Integer, ArrayList<Double>> getAllDataSeries(Integer
	// component_index, String element_name)
	// {
	// HashMap<Integer, ArrayList<Double>> series = new HashMap<Integer,
	// ArrayList<Double>>();
	// for (Integer componentIndex : dataStates.keySet())
	// {
	// ArrayList<Double> ser = getDataSeries(componentIndex, element_name);
	// if (ser != null)
	// {
	// series.put(component_index, ser);
	// }
	// }
	// return series;
	// }
	//
	// public <T> ArrayList<T> getDataSeries(Integer component_index, String
	// element_name)
	// {
	// //
	// if
	// (dataStates.get(component_index).getProperties().getName().equals(element_name))
	// {
	// return dataStates.get(component_index).getPreviousValues();
	// }
	//
	// return null;
	// }
	//
	// public ArrayList<Integer> hasData(String element_name)
	// {
	// ArrayList<Integer> series = new ArrayList<Integer>();
	// for (Integer component_index : dataStates.keySet())
	// {
	// if
	// (dataStates.get(component_index).getProperties().getName().equals(element_name))
	// {
	// series.add(component_index);
	// }
	// }
	// return series;
	// }
}
