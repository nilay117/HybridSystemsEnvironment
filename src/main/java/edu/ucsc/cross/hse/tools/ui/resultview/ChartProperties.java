package edu.ucsc.cross.hse.tools.ui.resultview;

import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.exe.access.ObjectManipulator;
import edu.ucsc.cross.hse.core.obj.config.DataSettings;
import edu.ucsc.cross.hse.core.obj.data.DataSet;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import org.jfree.data.xy.XYDataset;

@Loggable(Loggable.TRACE)
public class ChartProperties
{

	/*
	 * Default simulation time variable name
	 */
	public static String simulationTimeVarName = "Simulation Time";

	private HashMap<Object, ArrayList<ObjectManipulator>> manipulatorMap;
	private DataSet allData;
	private ArrayList<XYDataset> data;
	private ArrayList<String> yFilters;
	private String xFilter;
	private String xAxisLabel;
	private String yAxisLabel;
	private Font labelFont;

	public ChartProperties(DataSet all_data)
	{
		allData = all_data;
		initialize();

	}

	private void initialize()
	{

		data = new ArrayList<XYDataset>();
		yFilters = new ArrayList<String>();
		xFilter = simulationTimeVarName;
		initializeManipulatorMap();
	}

	private void initializeManipulatorMap()
	{
		manipulatorMap = new HashMap<Object, ArrayList<ObjectManipulator>>();
		for (ObjectManipulator e : allData.getGlobalStateData().keySet())
		{
			if (manipulatorMap.containsKey(e.getParent()))
			{
				manipulatorMap.get(e.getParent()).add(e);
			} else
			{
				ArrayList<ObjectManipulator> mans = new ArrayList<ObjectManipulator>();
				mans.add(e);
				manipulatorMap.put(e.getParent(), mans);
			}
		}
	}

	public boolean fufilsFilters(ObjectManipulator e)
	{
		boolean does = false;
		if (this.getyFilters().size() == 0 || this.getyFilters().contains(e.getField().getName()))
		{
			if (this.getxFilter().contains(simulationTimeVarName))
			{
				return true;
			} else
			{

				if (getXCounter(e) != null)
				{
					return true;
				}
			}
		}
		return does;
	}

	public ObjectManipulator getXCounter(ObjectManipulator e)
	{
		ObjectManipulator man = null;
		for (ObjectManipulator ee : manipulatorMap.get(e))
		{
			if (ee.getField().getName().contains(this.getxFilter()))
			{
				man = ee;
				return man;
			}
		}

		return man;

	}

	public ArrayList<XYDataset> getData()
	{
		return data;
	}

	public void setData(ArrayList<XYDataset> data)
	{
		this.data = data;
	}

	public void addToYFilter(String y_filter)
	{

		yFilters.add(y_filter);
	}

	public ArrayList<String> getyFilters()
	{
		return yFilters;
	}

	public void setyFilters(ArrayList<String> yFilters)
	{
		this.yFilters = yFilters;
	}

	public String getxFilter()
	{
		return xFilter;
	}

	public void setxFilter(String xFilter)
	{
		this.xFilter = xFilter;
	}

	public String getxAxisLabel()
	{
		return xAxisLabel;
	}

	public void setxAxisLabel(String xAxisLabel)
	{
		this.xAxisLabel = xAxisLabel;
	}

	public String getyAxisLabel()
	{
		return yAxisLabel;
	}

	public void setyAxisLabel(String yAxisLabel)
	{
		this.yAxisLabel = yAxisLabel;
	}

	public Font getLabelFont()
	{
		return labelFont;
	}

	public void setLabelFont(Font labelFont)
	{
		this.labelFont = labelFont;
	}

	public void clearYAxis()
	{
		this.yFilters.clear();
		// this.yFilters.addAll(getFieldNames());

	}

	public void clearXAxis()
	{
		this.xFilter = simulationTimeVarName;

	}

	public void clearXYAxis()
	{
		clearYAxis();
		clearXAxis();

	}

	public ArrayList<String> getFieldNames()
	{
		ArrayList<String> fieldNames = new ArrayList<String>();
		for (ObjectManipulator e : allData.getGlobalStateData().keySet())
		{

			if (!fieldNames.contains(e.getField().getName()))
			{
				fieldNames.add(e.getField().getName());
			}
		}
		return fieldNames;
	}
}
