package edu.ucsc.cross.hse.core.task;

import javafx.application.Application;
import javafx.stage.Stage;

public abstract class TaskManager extends Application implements TaskQueue
{

	private static TaskManager manager;

	public TaskManager()
	{

	}

	public Stage getNewStage()
	{
		return new Stage();
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

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		manager = this;
		taskQueue();
	}

	public static void runOp(TaskQueue item)
	{
		// itemm = item;
		launch();
	}

}
