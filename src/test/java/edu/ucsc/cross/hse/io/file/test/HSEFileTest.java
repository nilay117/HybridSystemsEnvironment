package edu.ucsc.cross.hse.io.file.test;

public class HSEFileTest
{

	public static final String testDirectoryPath = "src/test/resources/";
	public static final String testDirectory = "fileTest/";
	public static final String testFile = "testFile";

	// @Test
	// public void testEmptyLoadSave()
	// {
	// EnvironmentFile file = new EnvironmentFile();
	// file.writeToFile(new File(testDirectoryPath + testDirectory + testFile));
	// File retrievedFile = new File(testDirectoryPath + testDirectory + testFile + ".hse");
	// assertTrue(retrievedFile.exists());
	// EnvironmentFile retrievedHSEFile = EnvironmentFile.readFromFile(retrievedFile);
	// assertTrue(retrievedHSEFile != null);
	//
	// }
	//
	// @Test
	// public void testEnvironmentLoadSave()
	// {
	// Environment env = new Environment();
	// env.save(new File(testDirectoryPath + testDirectory + testFile));
	// File retrievedFile = new File(testDirectoryPath + testDirectory + testFile + ".hse");
	// assertTrue(retrievedFile.exists());
	// EnvironmentFile retrieved = EnvironmentFile.readFromFile(retrievedFile);
	// assertTrue(retrieved != null);
	// assertTrue(retrieved.get(EnvironmentFile.CONTENT) != null);
	// assertTrue(retrieved.get(EnvironmentFile.ENVIRONMENT) != null);
	// assertTrue(retrieved.get(EnvironmentFile.SETTINGS) != null);
	// assertTrue(retrieved.getAll(EnvironmentFile.SYSTEMS) != null);
	// assertTrue(retrieved.get(EnvironmentFile.DATA) != null);
	//
	// }
}
