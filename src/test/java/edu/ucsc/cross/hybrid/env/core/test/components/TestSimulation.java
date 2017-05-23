package edu.ucsc.cross.hybrid.env.core.test.components;

import edu.ucsc.cross.hybrid.env.core.processing.Environment;

public class TestSimulation
{

	public static void test()
	{
		TestSystem ss = new TestSystem();
		Environment environment = new Environment();
		//Component ss = Component.getComponentFromFile(
		//"/Users/beshort/Dropbox/Work/Code/HybridSystemsEnvironmentDevelopment/Toolbox/src/main/resources/xmlLibs/data/source/99.xml");
		//environment.getEnvironment().addComponent(breaker, 3);
		//environment.getEnvironment().addComponent(ballSystem, to_add);
		//environment.addSystem(breaker, to_add);
		//environment.addSystem(ballSystem, to_add);

		//	environment.start();
		// Launch Viewer (Optional)
		environment.getEnvironment().addComponent(ss, 1);
		environment.start();
	}

	public static void main(String args[])
	{
		//tryBreaker(25);
		test();
	}
}
