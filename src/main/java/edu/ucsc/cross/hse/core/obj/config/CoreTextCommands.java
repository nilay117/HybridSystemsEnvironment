package edu.ucsc.cross.hse.core.obj.config;

import edu.ucsc.cross.hse.core.exe.operator.HybridEnvironment;
import java.io.File;

public enum CoreTextCommands implements TextInputCommand
{
	LOAD_ENVIRONMENT(
		"-le",
		"Load environment");

	String inputFlag;
	String description;

	private CoreTextCommands(String input_flag, String description)
	{
		inputFlag = input_flag;
		this.description = description;
	}

	@Override
	public String getFlag()
	{
		// TODO Auto-generated method stub
		return inputFlag;
	}

	@Override
	public String getDescription()
	{
		// TODO Auto-generated method stub
		return description;
	}

	public void performAction(HybridEnvironment env, String arg)
	{
		switch (this)
		{
		case LOAD_ENVIRONMENT:
			HybridEnvironment envNew = HybridEnvironment.load(new File(arg));
			env.loadNewEnv(envNew);
			break;
		default:
			break;

		}
	}

}
