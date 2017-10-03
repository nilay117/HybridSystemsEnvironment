package edu.ucsc.cross.hse.core.logging;

import bs.commons.objects.access.CallerRetriever;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralLogger
{

	public static boolean printDebug;

	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSSS");
	public LevelConfiguration logConfig;

	public GeneralLogger(LevelConfiguration log_config)
	{
		logConfig = log_config;
	}

	public GeneralLogger()
	{
		logConfig = new LevelConfiguration();
	}

	/*
	 * 
	 * @see org.apache.maven.plugin.logging.Log#debug(java.lang.CharSequence)
	 */
	public void debug(CharSequence content)
	{
		print("debug", content);
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#debug(java.lang.CharSequence, java.lang.Throwable)
	 */
	public void debug(CharSequence content, Throwable error)
	{
		print("debug", content, error);
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#debug(java.lang.Throwable)
	 */
	public void debug(Throwable error)
	{
		print("debug", error);
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#info(java.lang.CharSequence)
	 */
	public void info(CharSequence content)
	{
		print("info", content);
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#info(java.lang.CharSequence, java.lang.Throwable)
	 */
	public void info(CharSequence content, Throwable error)
	{
		print("info", content, error);
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#info(java.lang.Throwable)
	 */
	public void info(Throwable error)
	{
		print("info", error);
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#warn(java.lang.CharSequence)
	 */
	public void warn(CharSequence content)
	{
		print("warn", content);
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#warn(java.lang.CharSequence, java.lang.Throwable)
	 */
	public void warn(CharSequence content, Throwable error)
	{
		print("warn", content, error);
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#warn(java.lang.Throwable)
	 */
	public void warn(Throwable error)
	{
		print("warn", error);
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#error(java.lang.CharSequence)
	 */
	public void error(CharSequence content)
	{
		System.err.println("[error] " + content.toString());
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#error(java.lang.CharSequence, java.lang.Throwable)
	 */
	public void error(CharSequence content, Throwable error)
	{
		StringWriter sWriter = new StringWriter();
		PrintWriter pWriter = new PrintWriter(sWriter);

		error.printStackTrace(pWriter);

		System.err.println("[error] " + content.toString() + "\n\n" + sWriter.toString());
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#error(java.lang.Throwable)
	 */
	public void error(Throwable error)
	{
		StringWriter sWriter = new StringWriter();
		PrintWriter pWriter = new PrintWriter(sWriter);

		error.printStackTrace(pWriter);

		System.err.println("[error] " + sWriter.toString());
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#isDebugEnabled()
	 */
	public boolean isDebugEnabled()
	{
		return logConfig.printDebug;
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#isInfoEnabled()
	 */
	public boolean isInfoEnabled()
	{
		return logConfig.printInfo;
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#isWarnEnabled()
	 */
	public boolean isWarnEnabled()
	{
		return logConfig.printWarning;
	}

	/**
	 * @see org.apache.maven.plugin.logging.Log#isErrorEnabled()
	 */
	public boolean isErrorEnabled()
	{
		return logConfig.printError;
	}

	private void print(String prefix, CharSequence content)
	{
		printStatement("[" + prefix + "] " + content.toString());
	}

	private void print(String prefix, Throwable error)
	{
		StringWriter sWriter = new StringWriter();
		PrintWriter pWriter = new PrintWriter(sWriter);

		error.printStackTrace(pWriter);

		printStatement("[" + prefix + "] " + sWriter.toString());
	}

	private void print(String prefix, CharSequence content, Throwable error)
	{
		StringWriter sWriter = new StringWriter();
		PrintWriter pWriter = new PrintWriter(sWriter);

		error.printStackTrace(pWriter);

		printStatement("[" + prefix + "] " + content.toString() + "\n\n" + sWriter.toString());
	}

	private void printStatement(String statement)
	{
		String print = sdf.format(new Date()) + " " + statement;
		System.out.println(print);

	}

}
