package edu.ucsc.cross.hse.core.obj.config;

import edu.ucsc.cross.hse.core.exe.operator.HybridEnvironment;

public interface TextInputCommand
{

	public String getFlag();

	public String getDescription();

	public void performAction(HybridEnvironment env, String arg);
}
