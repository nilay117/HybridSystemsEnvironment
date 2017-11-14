package edu.ucsc.cross.hse.core.task;

import javafx.application.Application;
import javafx.stage.Stage;

public abstract class TaskManager extends Application implements TaskQueue
{

	private static TaskManager manager; // static access to task manager instance

	/*
	 * Creates a new stage
	 */
	public Stage getNewStage()
	{
		return new Stage();
	}

	/*
	 * Starts the task manager application
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		manager = this;
		taskQueue();
	}

	public static Stage createStage()
	{
		if (manager != null)
		{
			return manager.getNewStage();
		} else
		{
			return null;
		}
	}

	public static void runOp(TaskQueue item)
	{
		// itemm = item;
		launch();
	}

}
