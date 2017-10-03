package edu.ucsc.cross.hse.example.datagenerator;

import edu.ucsc.cross.hse.core.exe.operator.HybridEnvironment;
import edu.ucsc.cross.hse.core.obj.structure.HybridSystem;
import edu.ucsc.cross.hse.tools.ui.resultview.PlotGenerator;
import org.apache.commons.math3.special.Gamma;

public class DataGeneratorSystem extends HybridSystem<DataGeneratorState>
{

	public DataGeneratorProperties params; // data generator parameters

	// Constructor that loads data generator state and parameters
	public DataGeneratorSystem(DataGeneratorState state, DataGeneratorProperties parameters)
	{
		super(state); // load data generator state
		params = parameters; // load data generator parameters
	}

	@Override
	public boolean C(DataGeneratorState x)
	{
		boolean waiting = x.timeToNextData > 0.0;
		return waiting;
	}

	@Override
	public void F(DataGeneratorState x, DataGeneratorState x_dot)
	{
		x_dot.dataGenerated = 0.0;
		x_dot.timeToNextData = -1.0;
	}

	@Override
	public boolean D(DataGeneratorState x)
	{
		boolean dataGenerated = x.timeToNextData <= 0.0;
		return dataGenerated;
	}

	@Override
	public void G(DataGeneratorState x, DataGeneratorState x_plus)
	{
		x_plus.dataGenerated = x.dataGenerated + params.dataItemSize;
		x_plus.timeToNextData = (.2 + .8 * Math.random()) * params.generationInterval;
	}

	public static void main(String args[])
	{
		HybridEnvironment env = new HybridEnvironment();
		DataGeneratorProperties p = new DataGeneratorProperties(1.0, 1.0);
		DataGeneratorState state = new DataGeneratorState(0, 1.0);
		DataGeneratorSystem sys = new DataGeneratorSystem(state, p);
		env.addContent(sys, 5);
		env.start(10.0, 500);
		env.openResultView();
		env.openResultView();
	}

}
