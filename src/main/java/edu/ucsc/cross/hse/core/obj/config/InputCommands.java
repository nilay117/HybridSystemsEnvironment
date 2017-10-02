package edu.ucsc.cross.hse.core.obj.config;

import edu.ucsc.cross.hse.core.exe.operator.HybridEnvironment;
import java.util.HashMap;

public class InputCommands
{

	private static HashMap<String, TextInputCommand> inputs = getCoreInputs();

	private static HashMap<String, TextInputCommand> getCoreInputs()
	{
		HashMap<String, TextInputCommand> core = new HashMap<String, TextInputCommand>();
		for (CoreTextCommands com : CoreTextCommands.values())
		{
			core.put(com.getFlag(), com);
		}
		return core;
	}

	public static HashMap<TextInputCommand, String> parsedInput(String args[])
	{
		HashMap<TextInputCommand, String> parse = new HashMap<TextInputCommand, String>();
		int i = 0;
		try
		{

			while (i < args.length)
			{
				System.out.println(args[i] + " " + args[i + 1]);
				TextInputCommand com = inputs.get(args[i]);
				parse.put(com, args[i + 1]);
				i = i + 2;
			}
		} catch (Exception badArgs)
		{

		}
		return parse;
	}

	public static HybridEnvironment getEnv(String args[])
	{
		HybridEnvironment env = new HybridEnvironment();
		HashMap<TextInputCommand, String> parsedInput = parsedInput(args);
		for (TextInputCommand in : parsedInput.keySet())
		{
			in.performAction(env, parsedInput.get(in));
		}
		return env;
	}

	public static void main(String args[])
	{
		HybridEnvironment env = getEnv(args);
		env.start(true);
	}
}
